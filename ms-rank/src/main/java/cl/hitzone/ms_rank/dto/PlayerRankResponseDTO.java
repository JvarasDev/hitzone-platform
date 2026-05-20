package cl.hitzone.ms_rank.dto;

import cl.hitzone.ms_rank.model.PlayerRank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class PlayerRankResponseDTO {
    private Long id;
    private String username;
    private String rankName;
    private Integer rankNumber;
    private Integer rrPoints;
    private Integer wins;
    private Integer losses;
    private Timestamp updatedAt;

    public PlayerRankResponseDTO(PlayerRank playerRank) {
        this.id = playerRank.getId();
        this.username = playerRank.getUsername();
        this.rankName = playerRank.getRankName();
        this.rankNumber = playerRank.getRankNumber();
        this.rrPoints = playerRank.getRrPoints();
        this.wins = playerRank.getWins();
        this.losses = playerRank.getLosses();
        this.updatedAt = playerRank.getUpdatedAt();
    }
}
