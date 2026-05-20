package cl.hitzone.ms_matches.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchPlayerDTO {
    private Long id;
    @NotBlank(message = "El username es obligatorio")
    private String username;
    private String agentName;
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer rrChange;

}
