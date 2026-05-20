package cl.hitzone.ms_maps.dto;

import cl.hitzone.ms_maps.model.typeDifficulty;
import cl.hitzone.ms_maps.model.typeMaps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO DE SALIDA - MapResponseDTO
 *
 * ¿QUÉ CONTIENE?
 * Todos los datos que DEVOLVERÁ el API al cliente
 *
 * DIFERENCIA CON MapRequestDTO:
 * - INCLUYE: id (generado por BD), active (estado actual)
 * - EXCLUYE: campos sensibles no necesarios para el cliente
 *
 * EJEMPLO DE RESPUESTA JSON:
 * {
 * "id": 1,
 * "name": "Dust2",
 * "type": "COMPETITIVE",
 * "difficulty": "HARD",
 * "description": "Mapa clásico de CS",
 * "totalSite": 2,
 * "active": true
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapResponseDTO {

    // ID generado por la BD
    private Long id;

    // Nombre del mapa
    private String name;

    // Tipo: COMPETITIVE, UNRATED, CUSTOM
    private typeMaps type;

    // Dificultad: EASY, MEDIUM, HARD
    private typeDifficulty difficulty;

    // Descripción
    private String description;

    // Total de sitios
    private Integer totalSite;

    // Estado: activo o inactivo
    private boolean active;
}
