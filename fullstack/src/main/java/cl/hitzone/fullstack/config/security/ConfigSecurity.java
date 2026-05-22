package cl.hitzone.fullstack.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuracion central de Spring Security para ms-auth.
 *
 * Política de rutas:
 *  - /api/auth/login     → público (genera el JWT)
 *  - /api/auth/register  → público (crea el usuario)
 *  - /api/auth/validate  → público (el api-gateway lo llama para validar tokens)
 *  - Todo lo demás       → requiere JWT válido
 *
 * IMPORTANTE: El api-gateway es el único punto de entrada externo.
 * Este SecurityFilterChain solo aplica para llamadas que lleguen
 * directamente a ms-auth (p.ej. llamadas internas del gateway a /api/auth/validate).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfigSecurity {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Cadena de filtros de seguridad.
     *
     * Rutas públicas (sin autenticación):
     *   - /api/auth/login     → el cliente pide un token
     *   - /api/auth/register  → el cliente crea una cuenta
     *   - /api/auth/validate  → el api-gateway valida un token existente
     *   - /actuator/**        → métricas de monitoreo
     *   - archivos estáticos  → frontend servido junto al backend
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ── Rutas públicas — sin token ─────────────────────────────────
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        // CRÍTICO: el gateway llama este endpoint para validar tokens
                        .requestMatchers("/api/auth/validate").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Archivos estáticos (frontend integrado si aplica)
                        .requestMatchers("/*.html", "/css/**", "/js/**", "/images/**").permitAll()
                        // ── Todo lo demás requiere JWT válido ─────────────────────────
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS — permite que el frontend (en otro puerto/dominio) haga requests al backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Proveedor DAO: conecta UserDetailsService y BCrypt con Spring Security.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * BCrypt con strength 8 (tradeoff consciente: velocidad vs seguridad).
     * Seguro mientras las contraseñas sigan el patrón fuerte del DTO de registro.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    /**
     * AuthenticationManager expuesto como Bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
