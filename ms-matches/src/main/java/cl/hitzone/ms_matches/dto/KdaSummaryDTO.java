package cl.hitzone.ms_matches.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KdaSummaryDTO {
    private Integer kills;
    private Integer deaths;
    private Integer assists;
}
