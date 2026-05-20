package cl.hitzone.ms_rank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlayerRankRequestDTO {

    @NotBlank(message = "Username es requerido")
    @Size(max = 100, message = "Username no puede superar 100 caracteres")
    private String username;

    @NotNull(message = "RR points es requerido")
    @Min(value = 0, message = "RR points no puede ser negativo")
    private Integer rrPoints;

    @NotNull(message = "Wins es requerido")
    @Min(value = 0, message = "Wins no puede ser negativo")
    private Integer wins;

    @NotNull(message = "Losses es requerido")
    @Min(value = 0, message = "Losses no puede ser negativo")
    private Integer losses;
}
