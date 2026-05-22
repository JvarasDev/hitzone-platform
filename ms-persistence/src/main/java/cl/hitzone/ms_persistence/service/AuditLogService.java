package cl.hitzone.ms_persistence.service;

import cl.hitzone.ms_persistence.dto.AuditLogRequestDTO;
import cl.hitzone.ms_persistence.dto.AuditLogResponseDTO;

import java.util.List;
import java.util.Map;

public interface AuditLogService {
    AuditLogResponseDTO registrarEvento(AuditLogRequestDTO requestDTO);
    List<AuditLogResponseDTO> listarTodos();
    List<AuditLogResponseDTO> listarPorServicio(String serviceName);
    List<AuditLogResponseDTO> listarPorUsuario(String username);
    Map<String, Long> countLogsByAction();
}

