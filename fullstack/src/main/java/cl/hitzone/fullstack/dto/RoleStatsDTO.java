package cl.hitzone.fullstack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleStatsDTO {
    private String role;
    private Long count;
}
