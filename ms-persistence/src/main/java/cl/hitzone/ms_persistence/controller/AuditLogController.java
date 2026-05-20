package cl.hitzone.ms_persistence.controller;

import cl.hitzone.ms_persistence.dto.AuditLogRequestDTO;
import cl.hitzone.ms_persistence.dto.AuditLogResponseDTO;
import cl.hitzone.ms_persistence.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // POST /api/v1/audit — Registrar un evento de auditoría
    @PostMapping
    public ResponseEntity<AuditLogResponseDTO> registrar(
            @Valid @RequestBody AuditLogRequestDTO requestDTO) {
        AuditLogResponseDTO response = auditLogService.registrarEvento(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/v1/audit — Listar todos los logs
    @GetMapping
    public ResponseEntity<List<AuditLogResponseDTO>> listarTodos() {
        return ResponseEntity.ok(auditLogService.listarTodos());
    }

    // GET /api/v1/audit/service/{name} — Logs filtrados por microservicio
    @GetMapping("/service/{name}")
    public ResponseEntity<List<AuditLogResponseDTO>> listarPorServicio(
            @PathVariable String name) {
        return ResponseEntity.ok(auditLogService.listarPorServicio(name));
    }

    // GET /api/v1/audit/user/{username} — Logs filtrados por usuario
    @GetMapping("/user/{username}")
    public ResponseEntity<List<AuditLogResponseDTO>> listarPorUsuario(
            @PathVariable String username) {
        return ResponseEntity.ok(auditLogService.listarPorUsuario(username));
    }
}

