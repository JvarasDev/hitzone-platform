package cl.hitzone.fullstack.controller;

import cl.hitzone.fullstack.dto.AuthResponseDTO;
import cl.hitzone.fullstack.dto.LoginRequestDTO;
import cl.hitzone.fullstack.dto.RoleStatsDTO;
import cl.hitzone.fullstack.dto.UserRegistrationDTO;
import cl.hitzone.fullstack.dto.UserResponseDTO;
import cl.hitzone.fullstack.Service.AuthService;
import cl.hitzone.fullstack.config.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // ── REGISTRO ─────────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationDTO registrationRequest) {
        UserResponseDTO newUser = authService.registerUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario '" + newUser.getUsername() + "' registrado correctamente.");
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        UserResponseDTO user = authService.authenticate(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
        return ResponseEntity.ok(new AuthResponseDTO(token, user.getUsername()));
    }

    // ── VALIDATE ─────────────────────────────────────────────────────────────
    /**
     * Endpoint de validación de tokens JWT.
     *
     * El api-gateway (y potencialmente otros servicios internos) llama a este
     * endpoint para verificar que un Bearer token sea válido y no haya expirado.
     *
     * Ruta: GET /api/auth/validate
     * Header requerido: Authorization: Bearer <token>
     *
     * Respuestas:
     *   200 OK  → {"valid": true, "username": "<username>"}
     *   401     → Spring Security rechaza antes de llegar aquí si no hay token
     *             (pero este endpoint es público, así que el filtro JWT deja pasar)
     *
     * NOTA: Este endpoint está marcado como público en ConfigSecurity para que
     * el gateway pueda llamarlo sin necesitar autenticarse a sí mismo.
     * La validación real la hace JwtUtil.
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "valid", false,
                            "error", "Header Authorization ausente o malformado"
                    ));
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            // validateToken verifica firma + expiración
            boolean isValid = jwtUtil.validateToken(token, username);
            if (isValid) {
                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", username
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "valid", false,
                                "error", "Token inválido o expirado"
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "valid", false,
                            "error", "Token inválido: " + e.getMessage()
                    ));
        }
    }

    // ── REPORTES ─────────────────────────────────────────────────────────────

    @GetMapping("/stats/users/count")
    public ResponseEntity<Map<String, Long>> getTotalUsersCount() {
        long count = authService.getTotalUsersCount();
        return ResponseEntity.ok(Map.of("totalUsers", count));
    }

    @GetMapping("/stats/users/by-role")
    public ResponseEntity<List<RoleStatsDTO>> getUsersCountByRole() {
        return ResponseEntity.ok(authService.getUsersCountByRole());
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsersByUsername(@RequestParam("username") String username) {
        return ResponseEntity.ok(authService.searchUsersByUsername(username));
    }
}
