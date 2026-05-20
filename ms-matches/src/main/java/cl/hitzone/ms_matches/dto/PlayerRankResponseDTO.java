package cl.hitzone.ms_matches.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerRankResponseDTO {
    private Long id;
    private String username;
    private String rankName;
    private Integer rankNumber;
    private Integer rrPoints;
    private Integer wins;
    private Integer losses;
    private Date updatedAt;
}
