package cl.hitzone.fullstack.Service;

import cl.hitzone.fullstack.Model.Role;
import cl.hitzone.fullstack.Model.User;
import cl.hitzone.fullstack.Repository.RoleRepository;
import cl.hitzone.fullstack.Repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.hitzone.fullstack.dto.RoleStatsDTO;
import cl.hitzone.fullstack.dto.UserRegistrationDTO;
import cl.hitzone.fullstack.dto.UserResponseDTO;
import cl.hitzone.fullstack.exception.DuplicateResourceException;
import cl.hitzone.fullstack.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de autenticación.
 *
 * Centraliza la lógica de negocio relacionada con usuarios:
 * - Registro de nuevos usuarios (con validaciones de unicidad)
 * - Autenticación (comparación de contraseñas con BCrypt)
 *
 * IMPORTANTE: No manejamos sesiones ni tokens aquí.
 * La generación del JWT queda en el controller para mantener
 * separadas las responsabilidades (SRP - Single Responsibility Principle).
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * Validaciones previas al guardado:
     * 1. Email único — no pueden existir dos usuarios con el mismo email
     * 2. Username único — el nombre de usuario es un identificador único
     *
     * La contraseña se encripta con BCrypt antes de persistirla.
     * Nunca guardamos contraseñas en texto plano.
     */
    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO dto) {

        // Verificamos que el email no esté ya registrado
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("El email ya está registrado");
        }

        // Verificamos que el username no esté ya en uso
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Este nombre de usuario ya está en uso");
        }

        // Mapeamos el DTO a la entidad User
        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setEmail(dto.getEmail());

        // Encriptamos la contraseña con BCrypt antes de guardar.
        // BCrypt genera un salt aleatorio cada vez, por lo que el mismo password
        // genera hashes distintos — completamente normal y esperado.
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Asignamos el rol por defecto ROLE_USER desde la BD.
        // El rol debe existir previamente en la tabla "roles".
        Role defaultRole = roleRepository.findByNameRol("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Rol ROLE_USER no encontrado en la BD"));
        newUser.getRoles().add(defaultRole);

        return new UserResponseDTO(userRepository.save(newUser));
    }

    /**
     * Autentica un usuario verificando username y contraseña.
     *
     * Usamos passwordEncoder.matches() para comparar la contraseña en texto plano
     * con el hash BCrypt almacenado. NUNCA comparamos strings directamente.
     *
     * Si las credenciales son incorrectas, lanzamos RuntimeException
     * que el controller captura y convierte en 401 Unauthorized.
     */
    @Override
    @Transactional
    public UserResponseDTO authenticate(String username, String password) {

        // Buscamos al usuario por nombre de usuario
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));

        // matches(rawPassword, encodedPassword) — BCrypt hace la comparación segura
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new cl.hitzone.fullstack.exception.UnauthorizedException("Contraseña incorrecta");
        }

        return new UserResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleStatsDTO> getUsersCountByRole() {
        return userRepository.countUsersByRole().stream()
                .map(result -> new RoleStatsDTO((String) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username).stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }
}
