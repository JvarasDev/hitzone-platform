package cl.hitzone.ms_persistence.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequestDTO {

    @NotBlank(message = "El nombre del servicio es obligatorio")
    private String serviceName;

    @NotBlank(message = "La acción es obligatoria")
    private String action;

    // Opcionales — pueden ser null según el tipo de evento
    private String entityType;
    private Long   entityId;
    private String username;
    private String details;
    private String status; // Si no se envía, el model asigna "SUCCESS"
}

