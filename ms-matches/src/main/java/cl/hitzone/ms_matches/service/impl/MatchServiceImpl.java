package cl.hitzone.ms_matches.service.impl;

import cl.hitzone.ms_matches.dto.KdaSummaryDTO;
import cl.hitzone.ms_matches.dto.MatchPlayerDTO;
import cl.hitzone.ms_matches.dto.MatchRequestDTO;
import cl.hitzone.ms_matches.dto.MatchResponseDTO;
import cl.hitzone.ms_matches.exception.DuplicateResourceException;
import cl.hitzone.ms_matches.exception.ResourceNotFoundException;
import cl.hitzone.ms_matches.model.Match;
import cl.hitzone.ms_matches.model.MatchPlayer;
import cl.hitzone.ms_matches.repository.MatchPlayerRepository;
import cl.hitzone.ms_matches.repository.MatchRepository;
import cl.hitzone.ms_matches.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;

    // ─── GET ALL ───────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDTO> findAll() {
        return matchRepository.findAllOrderedByDate()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── GET BY ID (clave PK interna) ─────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public MatchResponseDTO findById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida no encontrada con ID: " + id));
        return toResponseDTO(match);
    }

    // ─── GET BY MATCH-ID (ID del servidor de juego) ───────────────────────────
    @Override
    @Transactional(readOnly = true)
    public MatchResponseDTO findByMatchId(String matchId) {
        Match match = matchRepository.findByMatchId(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida no encontrada con matchId: " + matchId));
        return toResponseDTO(match);
    }

    // ─── FILTER BY RESULT ─────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDTO> findByResult(String result) {
        return matchRepository.findByResultOrderedByDate(result.toUpperCase())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── FILTER BY GAME MODE ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDTO> findByGameMode(String gameMode) {
        return matchRepository.findByGameMode(gameMode.toUpperCase())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── FILTER BY MAP ────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDTO> findByMap(String mapName) {
        return matchRepository.findByMapName(mapName)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── PLAYER HISTORY (por username) ────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<MatchPlayerDTO> findPlayersByUsername(String username) {
        return matchPlayerRepository.findByUsername(username)
                .stream()
                .map(this::toPlayerDTO)
                .collect(Collectors.toList());
    }

    // ─── CREATE MATCH + PLAYERS (transacción atómica) ─────────────────────────
    @Override
    @Transactional
    public MatchResponseDTO create(MatchRequestDTO dto) {
        if (matchRepository.findByMatchId(dto.getMatchId()).isPresent()) {
            throw new DuplicateResourceException("Ya existe una partida con matchId: " + dto.getMatchId());
        }

        Match match = Match.builder()
                .matchId(dto.getMatchId())
                .mapName(dto.getMapName())
                .gameMode(dto.getGameMode().toUpperCase())
                .result(dto.getResult().toUpperCase())
                .scoreTeam(dto.getScoreTeam())
                .scoreEnemy(dto.getScoreEnemy())
                .durationS(dto.getDurationS())
                .build();

        // Guardar primero el match para obtener su ID generado
        Match saved = matchRepository.save(match);

        // Crear y asociar los jugadores
        if (dto.getPlayers() != null && !dto.getPlayers().isEmpty()) {
            List<MatchPlayer> players = dto.getPlayers().stream().map(p -> MatchPlayer.builder()
                    .match(saved)
                    .username(p.getUsername())
                    .agentName(p.getAgentName())
                    .kills(p.getKills() != null ? p.getKills() : 0)
                    .deaths(p.getDeaths() != null ? p.getDeaths() : 0)
                    .assists(p.getAssists() != null ? p.getAssists() : 0)
                    .rrChange(p.getRrChange())
                    .build()
            ).collect(Collectors.toList());
            matchPlayerRepository.saveAll(players);
            saved.setPlayers(players);
        }

        return toResponseDTO(saved);
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida no encontrada con ID: " + id));
        matchRepository.delete(match);
    }

    // ─── REPORTE: Contar partidas por resultado ────────────────────────────────
    @Transactional(readOnly = true)
    public Long countByResult(String result) {
        return matchRepository.countByResult(result.toUpperCase());
    }

    // ─── REPORTE: Obtener KDA del jugador ─────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public KdaSummaryDTO getPlayerKdaSummary(String username) {
        List<MatchPlayerDTO> matches = findPlayersByUsername(username);
        int totalKills = matches.stream().mapToInt(MatchPlayerDTO::getKills).sum();
        int totalDeaths = matches.stream().mapToInt(MatchPlayerDTO::getDeaths).sum();
        int totalAssists = matches.stream().mapToInt(MatchPlayerDTO::getAssists).sum();
        
        return KdaSummaryDTO.builder()
                .kills(totalKills)
                .deaths(totalDeaths)
                .assists(totalAssists)
                .build();
    }

    // ─── MAPPINGS ─────────────────────────────────────────────────────────────
    private MatchResponseDTO toResponseDTO(Match match) {
        List<MatchPlayerDTO> playerDTOs = null;
        if (match.getPlayers() != null) {
            playerDTOs = match.getPlayers().stream()
                    .map(this::toPlayerDTO)
                    .collect(Collectors.toList());
        }
        return MatchResponseDTO.builder()
                .id(match.getId())
                .matchId(match.getMatchId())
                .mapName(match.getMapName())
                .gameMode(match.getGameMode())
                .result(match.getResult())
                .scoreTeam(match.getScoreTeam())
                .scoreEnemy(match.getScoreEnemy())
                .durationS(match.getDurationS())
                .playedAt(match.getPlayedAt())
                .players(playerDTOs)
                .build();
    }

    private MatchPlayerDTO toPlayerDTO(MatchPlayer p) {
        return MatchPlayerDTO.builder()
                .id(p.getId())
                .username(p.getUsername())
                .agentName(p.getAgentName())
                .kills(p.getKills())
                .deaths(p.getDeaths())
                .assists(p.getAssists())
                .rrChange(p.getRrChange())
                .build();
    }
}
