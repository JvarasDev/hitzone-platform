package cl.hitzone.ms_rank.service;

import cl.hitzone.ms_rank.dto.PlayerRankRequestDTO;
import cl.hitzone.ms_rank.dto.PlayerRankResponseDTO;
import cl.hitzone.ms_rank.model.PlayerRank;
import cl.hitzone.ms_rank.repository.PlayerRankRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import cl.hitzone.ms_rank.exception.ResourceNotFoundException;
import cl.hitzone.ms_rank.exception.DuplicateResourceException;

@Service
public class PlayerRankServiceImpl implements PlayerRankService {

    private final PlayerRankRepository playerRankRepository;

    public PlayerRankServiceImpl(PlayerRankRepository playerRankRepository) {
        this.playerRankRepository = playerRankRepository;
    }

    // ─── GET ALL (ordenado por RR desc) ───────────────────────────
    public List<PlayerRankResponseDTO> getAllRanks() {
        return playerRankRepository.findAll(Sort.by(Sort.Direction.DESC, "rrPoints"))
                .stream()
                .map(PlayerRankResponseDTO::new)
                .collect(Collectors.toList());
    }

    // ─── GET BY USERNAME ──────────────────────────────────────────
    public PlayerRankResponseDTO getRankByUsername(String username) {
        PlayerRank player = playerRankRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado: " + username));
        return new PlayerRankResponseDTO(player);
    }

    // ─── CREATE ───────────────────────────────────────────────────
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
    public void deleteRank(String username) {
        PlayerRank player = playerRankRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado: " + username));
        playerRankRepository.delete(player);
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
