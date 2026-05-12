package cl.hitzone.ms_agents.dto;
import cl.hitzone.ms_agents.model.AbilityType;
import lombok.Data;

@Data
public class AbilityDTO {
    private Long id;
    private String name;
    private AbilityType type;
    private String description;
}
