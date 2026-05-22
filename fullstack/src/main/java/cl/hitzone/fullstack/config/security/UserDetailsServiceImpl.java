package cl.hitzone.fullstack.config.security;

import cl.hitzone.fullstack.Model.User;
import cl.hitzone.fullstack.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Implementación de UserDetailsService requerida por Spring Security.
 *
 * Spring Security necesita saber cómo obtener un usuario desde alguna fuente
 * de datos (BD, memoria, LDAP, etc.) para poder verificar identidades.
 * Esta clase es ese "puente" entre nuestra entidad User y el sistema de
 * seguridad.
 *
 * Se usa principalmente en el JwtAuthFilter para cargar el usuario
 * después de validar el token JWT.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

        @Autowired
        private UserRepository userRepository;

        /**
         * Carga el usuario desde la base de datos usando el username.
         * Si no existe, lanzamos UsernameNotFoundException que Spring Security
         * maneja internamente devolviendo 401.
         *
         * Convertimos nuestra entidad User a un UserDetails de Spring Security
         * mapeando los roles de la BD como SimpleGrantedAuthority.
         * Requiere que la relación @ManyToMany con Role sea EAGER.
         */
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado en la base de datos: " + username));

                // Convertimos cada Role de la BD en un GrantedAuthority que Spring Security
                // entiende.
                // Ej: rol con nameRol = "ROLE_ADMIN" → new SimpleGrantedAuthority("ROLE_ADMIN")
                var authorities = user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getNameRol()))
                                .collect(Collectors.toList());
                // revisar esta zona posiblmente cause los problemas en postman
                return org.springframework.security.core.userdetails.User
                                .withUsername(user.getUsername())
                                .password(user.getPassword()) // ya viene encriptada (BCrypt)
                                .authorities(authorities) // roles dinámicos desde la BD
                                .build();
        }
}
