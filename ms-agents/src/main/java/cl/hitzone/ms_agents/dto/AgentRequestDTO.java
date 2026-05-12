package cl.hitzone.ms_agents.dto;
import cl.hitzone.ms_agents.model.AgentRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class AgentRequestDTO {
    @NotBlank(message = "El nombre del agente es obligatorio")
    private String name;

    @NotNull(message = "El rol del agente es obligatorio")
    private AgentRole role;

    private String description;
    private String imageUrl;

    private List<AbilityDTO> abilities;
}
