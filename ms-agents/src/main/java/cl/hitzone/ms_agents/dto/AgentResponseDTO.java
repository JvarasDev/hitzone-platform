package cl.hitzone.ms_agents.dto;
import cl.hitzone.ms_agents.model.AgentRole;
import lombok.Data;
import java.util.List;

@Data
public class AgentResponseDTO {
    private Long id;
    private String name;
    private AgentRole role;
    private String description;
    private String imageUrl;
    private List<AbilityDTO> abilities;
}
