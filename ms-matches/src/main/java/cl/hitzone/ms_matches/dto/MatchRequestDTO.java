package cl.hitzone.ms_matches.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestDTO {
    @NotBlank(message = "El matchId es obligatorio")
    private String matchId;
    private String mapName;
    @NotBlank(message = "El gameMode es obligatorio")
    private String gameMode;
    @NotBlank(message = "El resultado es obligatorio")
    private String result;
    private Integer scoreTeam;
    private Integer scoreEnemy;
    private Integer durationS;
    @NotEmpty(message = "Debe incluir al menos un jugador")
    private List<MatchPlayerDTO> players;
}
