package cl.hitzone.ms_rank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankDistributionDTO {
    private String rankName;
    private Long count;
}
