package cl.hitzone.ms_weapons.dto;

import cl.hitzone.ms_weapons.model.WeaponCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeaponRequestDTO {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Category is required")
    private WeaponCategory category;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal damage;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal fireRate;

    @NotNull
    @Min(0)
    private Integer magazineSize;

    @NotNull
    @Min(0)
    @Max(10000)
    private Integer price;

    // Opcional
    private String description;
}
