package cl.hitzone.ms_weapons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeaponCategoryStatsDTO {
    private String category;
    private Long count;
}
