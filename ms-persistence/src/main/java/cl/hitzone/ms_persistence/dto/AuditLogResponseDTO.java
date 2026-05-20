package cl.hitzone.ms_persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {

    private Long          id;
    private String        serviceName;
    private String        action;
    private String        entityType;
    private Long          entityId;
    private String        username;
    private String        details;
    private String        status;
    private LocalDateTime createdAt;
}

