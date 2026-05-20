package cl.hitzone.ms_maps.dto;

import cl.hitzone.ms_maps.model.typeDifficulty;
import cl.hitzone.ms_maps.model.typeMaps;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapRequestDTO {

    // Nombre del mapa (requerido)
    @NotBlank(message = "El nombre del mapa es obligatorio")
    private String name;

    // Tipo de mapa: COMPETITIVE, UNRATED, CUSTOM
    @NotNull(message = "El tipo del mapa es obligatorio")
    private typeMaps type;

    // Dificultad: EASY, MEDIUM, HARD
    @NotNull(message = "La dificultad es obligatoria")
    private typeDifficulty difficulty;

    // Descripción (opcional)
    private String description;

    // Total de sitios del mapa
    @NotNull(message = "El total de sites es obligatorio")
    private Integer totalSite;
}
