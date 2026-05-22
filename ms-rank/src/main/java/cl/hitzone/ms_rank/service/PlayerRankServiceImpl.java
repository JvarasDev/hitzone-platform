package cl.hitzone.ms_rank.service;

import cl.hitzone.ms_rank.dto.PlayerRankRequestDTO;
import cl.hitzone.ms_rank.dto.PlayerRankResponseDTO;
import cl.hitzone.ms_rank.dto.RankDistributionDTO;
import cl.hitzone.ms_rank.model.PlayerRank;
import cl.hitzone.ms_rank.repository.PlayerRankRepository;
import cl.hitzone.ms_rank.exception.ResourceNotFoundException;
import cl.hitzone.ms_rank.exception.DuplicateResourceException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerRankServiceImpl implements PlayerRankService {

    private final PlayerRankRepository playerRankRepository;

    public PlayerRankServiceImpl(PlayerRankRepository playerRankRepository) {
        this.playerRankRepository = playerRankRepository;
    }

    // ─── GET ALL (ordenado por RR desc) ───────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<PlayerRankResponseDTO> getAllRanks() {
        return playerRankRepository.findAll(Sort.by(Sort.Direction.DESC, "rrPoints"))
                .stream()
                .map(PlayerRankResponseDTO::new)
                .collect(Collectors.toList());
    }

    // ─── GET BY USERNAME ──────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PlayerRankResponseDTO getRankByUsername(String username) {
        PlayerRank player = playerRankRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado: " + username));
        return new PlayerRankResponseDTO(player);
    }

    // ─── CREATE ───────────────────────────────────────────────────
    @Override
    @Transactional
    public PlayerRankResponseDTO createRank(PlayerRankRequestDTO dto) {
        if (playerRankRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("El username ya existe: " + dto.getUsername());
        }

        PlayerRank player = new PlayerRank();
        player.setUsername(dto.getUsername());
        player.setRrPoints(dto.getRrPoints());
        player.setWins(dto.getWins());
        player.setLosses(dto.getLosses());

        // Asignar rank según RR
        applyRank(player, dto.getRrPoints());

        return new PlayerRankResponseDTO(playerRankRepository.save(player));
    }

    // ─── UPDATE ───────────────────────────────────────────────────
    @Override
    @Transactional
    public PlayerRankResponseDTO updateRank(String username, PlayerRankRequestDTO dto) {
        PlayerRank player = playerRankRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado: " + username));

        player.setRrPoints(dto.getRrPoints());
        player.setWins(dto.getWins());
        player.setLosses(dto.getLosses());

        // Recalcular rank según nuevos RR
        applyRank(player, dto.getRrPoints());

        return new PlayerRankResponseDTO(playerRankRepository.save(player));
    }

    // ─── DELETE ───────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteRank(String username) {
        PlayerRank player = playerRankRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado: " + username));
        playerRankRepository.delete(player);
    }

    // ─── REPORT 1: GET TOP N PLAYERS ──────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<PlayerRankResponseDTO> getTopPlayers(int count) {
        return playerRankRepository.findAll(PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "rrPoints")))
                .getContent()
                .stream()
                .map(PlayerRankResponseDTO::new)
                .collect(Collectors.toList());
    }

    // ─── REPORT 2: GET RANK DISTRIBUTION ─────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RankDistributionDTO> getRankDistribution() {
        return playerRankRepository.getRankDistribution().stream()
                .map(result -> new RankDistributionDTO((String) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }

    // ─── REPORT 3: SEARCH PLAYERS ────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<PlayerRankResponseDTO> searchPlayersByUsername(String username) {
        return playerRankRepository.findByUsernameContainingIgnoreCase(username, Sort.by(Sort.Direction.ASC, "username"))
                .stream()
                .map(PlayerRankResponseDTO::new)
                .collect(Collectors.toList());
    }

    // ─── Lógica de ranking ────────────────────────────────────────
    private void applyRank(PlayerRank player, int rr) {
        if (rr >= 1000) {
            player.setRankName("RADIANT");
            player.setRankNumber(7);
        } else if (rr >= 800) {
            player.setRankName("DIAMOND");
            player.setRankNumber(6);
        } else if (rr >= 600) {
            player.setRankName("PLATINUM");
            player.setRankNumber(5);
        } else if (rr >= 400) {
            player.setRankName("GOLD");
            player.setRankNumber(4);
        } else if (rr >= 200) {
            player.setRankName("SILVER");
            player.setRankNumber(3);
        } else if (rr >= 100) {
            player.setRankName("BRONZE");
            player.setRankNumber(2);
        } else {
            player.setRankName("IRON");
            player.setRankNumber(1);
        }
    }
}
