package cl.hitzone.ms_matches.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResponseDTO {
    private Long id;
    private String matchId;
    private String mapName;
    private String gameMode;
    private String result;
    private Integer scoreTeam;
    private Integer scoreEnemy;
    private Integer durationS;
    private LocalDateTime playedAt;
    private List<MatchPlayerDTO> players;
}
