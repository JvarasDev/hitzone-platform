package cl.hitzone.ms_persistence.service;

import cl.hitzone.ms_persistence.dto.AuditLogRequestDTO;
import cl.hitzone.ms_persistence.dto.AuditLogResponseDTO;
import cl.hitzone.ms_persistence.model.AuditLog;
import cl.hitzone.ms_persistence.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // POST /api/v1/audit — Registrar un nuevo evento
    @Transactional
    public AuditLogResponseDTO registrarEvento(AuditLogRequestDTO requestDTO) {
        AuditLog log = new AuditLog();
        log.setServiceName(requestDTO.getServiceName());
        log.setAction(requestDTO.getAction());
        log.setEntityType(requestDTO.getEntityType());
        log.setEntityId(requestDTO.getEntityId());
        log.setUsername(requestDTO.getUsername());
        log.setDetails(requestDTO.getDetails());
        // Si el cliente no envía status, el @PrePersist del model asigna "SUCCESS"
        if (requestDTO.getStatus() != null) {
            log.setStatus(requestDTO.getStatus());
        }

        AuditLog guardado = auditLogRepository.save(log);
        return convertirADTO(guardado);
    }

    // GET /api/v1/audit — Listar todos los logs
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> listarTodos() {
        return auditLogRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // GET /api/v1/audit/service/{name} — Filtrar por microservicio
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> listarPorServicio(String serviceName) {
        return auditLogRepository.findByServiceName(serviceName)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // GET /api/v1/audit/user/{username} — Filtrar por usuario
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> listarPorUsuario(String username) {
        return auditLogRepository.findByUsername(username)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Convierte entidad → DTO de respuesta
    private AuditLogResponseDTO convertirADTO(AuditLog log) {
        return new AuditLogResponseDTO(
                log.getId(),
                log.getServiceName(),
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getUsername(),
                log.getDetails(),
                log.getStatus(),
                log.getCreatedAt()
        );
    }
}
