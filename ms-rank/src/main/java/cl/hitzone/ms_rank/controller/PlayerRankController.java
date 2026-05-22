package cl.hitzone.ms_rank.controller;

import cl.hitzone.ms_rank.dto.PlayerRankRequestDTO;
import cl.hitzone.ms_rank.dto.PlayerRankResponseDTO;
import cl.hitzone.ms_rank.dto.RankDistributionDTO;
import cl.hitzone.ms_rank.service.PlayerRankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rank")
public class PlayerRankController {
    private final PlayerRankService playerRankService;

    public PlayerRankController(PlayerRankService playerRankService) {
        this.playerRankService = playerRankService;
    }

    // ─── GET /api/v1/ranks ────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<PlayerRankResponseDTO>> getAllRanks() {
        return ResponseEntity.ok(playerRankService.getAllRanks());
    }

    // ─── GET /api/v1/ranks/{username} ─────────────────────────────
    @GetMapping("/{username}")
    public ResponseEntity<PlayerRankResponseDTO> getRankByUsername(@PathVariable String username) {
        return ResponseEntity.ok(playerRankService.getRankByUsername(username));
    }

    // ─── POST /api/v1/ranks ───────────────────────────────────────
    @PostMapping
    public ResponseEntity<PlayerRankResponseDTO> createRank(@Valid @RequestBody PlayerRankRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerRankService.createRank(dto));
    }

    // ─── PUT /api/v1/ranks/{username} ─────────────────────────────
    @PutMapping("/{username}")
    public ResponseEntity<PlayerRankResponseDTO> updateRank(
            @PathVariable String username,
            @Valid @RequestBody PlayerRankRequestDTO dto) {
        return ResponseEntity.ok(playerRankService.updateRank(username, dto));
    }

    // ─── DELETE /api/v1/ranks/{username} ──────────────────────────
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteRank(@PathVariable String username) {
        playerRankService.deleteRank(username);
        return ResponseEntity.noContent().build();
    }

    // ─── REPORT 1: GET TOP N PLAYERS ──────────────────────────────
    @GetMapping("/top/{count}")
    public ResponseEntity<List<PlayerRankResponseDTO>> getTopPlayers(@PathVariable int count) {
        return ResponseEntity.ok(playerRankService.getTopPlayers(count));
    }

    // ─── REPORT 2: GET RANK DISTRIBUTION ─────────────────────────
    @GetMapping("/distribution")
    public ResponseEntity<List<RankDistributionDTO>> getRankDistribution() {
        return ResponseEntity.ok(playerRankService.getRankDistribution());
    }

    // ─── REPORT 3: SEARCH PLAYERS ────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<List<PlayerRankResponseDTO>> searchPlayersByUsername(@RequestParam("username") String username) {
        return ResponseEntity.ok(playerRankService.searchPlayersByUsername(username));
    }
}
