package cl.hitzone.fullstack.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT — se ejecuta UNA VEZ por cada request HTTP (OncePerRequestFilter).
 *
 * Flujo que sigue cada request:
 * 1. Leer el header "Authorization"
 * 2. Si no existe o no empieza con "Bearer ", dejar pasar sin autenticar
 * 3. Extraer el token (sin el prefijo "Bearer ")
 * 4. Obtener el username del token usando JwtUtil
 * 5. Verificar que el token sea válido para ese usuario
 * 6. Crear un objeto de autenticación y registrarlo en el SecurityContext
 *
 * Este filtro se registra en ConfigSecurity ANTES del filtro estándar
 * UsernamePasswordAuthenticationFilter de Spring Security.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Leemos el header de autorización de la request
        final String authHeader = request.getHeader("Authorization");

        // Si el header no existe o no tiene formato "Bearer <token>", dejamos pasar
        // sin autenticar. Spring Security se encargará de rechazar si la ruta lo requiere.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token quitando el prefijo "Bearer " (7 caracteres)
        final String jwt = authHeader.substring(7);

        // Extraemos el username que está guardado en el "subject" del token
        final String username = jwtUtil.extractUsername(jwt);

        // Solo procesamos si obtuvimos un username Y el SecurityContext no tiene
        // ya una autenticación activa (para no sobreescribir una sesión válida)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargamos el usuario completo desde la BD para verificar que exista
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validamos el token: verifica firma y que no haya expirado
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                // Creamos el objeto de autenticación que Spring Security necesita.
                // Los "credentials" van como null porque con JWT no necesitamos contraseña aquí.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Adjuntamos detalles del request (IP, session id) al objeto de autenticación
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Registramos la autenticación en el contexto de seguridad.
                // A partir de aquí, Spring Security considera al usuario autenticado
                // para el resto del ciclo de vida de esta request.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuamos con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}
