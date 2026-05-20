package cl.hitzone.ms_matches.service;

import cl.hitzone.ms_matches.dto.KdaSummaryDTO;
import cl.hitzone.ms_matches.dto.MatchPlayerDTO;
import cl.hitzone.ms_matches.dto.MatchRequestDTO;
import cl.hitzone.ms_matches.dto.MatchResponseDTO;

import java.util.List;

/**
 * Contrato de negocio para el microservicio de partidas.
 * Define todas las operaciones disponibles sobre matches y sus jugadores.
 * La implementación concreta vive en MatchServiceImpl.
 */
public interface MatchService {

    /** Retorna todas las partidas ordenadas de más reciente a más antigua. */
    List<MatchResponseDTO>  findAll();

    /** Busca una partida por su clave primaria interna (auto-incremental de BD). */
    MatchResponseDTO         findById(Long id);

    /** Busca una partida por el ID que devuelve el servidor del juego (ej: "match-123abc"). */
    MatchResponseDTO         findByMatchId(String matchId);

    /** Filtra partidas por resultado: "WIN", "LOSS" o "DRAW". */
    List<MatchResponseDTO>  findByResult(String result);

    /** Filtra partidas por modo de juego: "COMPETITIVE", "UNRATED", etc. */
    List<MatchResponseDTO>  findByGameMode(String gameMode);

    /** Filtra partidas por nombre de mapa: "Ascent", "Bind", etc. */
    List<MatchResponseDTO>  findByMap(String mapName);

    /** Retorna todas las filas de MatchPlayer donde el username coincide (historial de un jugador). */
    List<MatchPlayerDTO>    findPlayersByUsername(String username);

    /** Crea una partida completa junto a su lista de jugadores (transacción atómica). */
    MatchResponseDTO         create(MatchRequestDTO dto);

    /** Elimina una partida por su ID (también elimina sus jugadores por CASCADE). */
    void                     delete(Long id);

    /** Obtiene el resumen KDA acumulado de un jugador. */
    KdaSummaryDTO            getPlayerKdaSummary(String username);
}
