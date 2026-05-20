package cl.hitzone.ms_matches.controller;

import cl.hitzone.ms_matches.dto.KdaSummaryDTO;
import cl.hitzone.ms_matches.dto.MatchPlayerDTO;
import cl.hitzone.ms_matches.dto.MatchRequestDTO;
import cl.hitzone.ms_matches.dto.MatchResponseDTO;
import cl.hitzone.ms_matches.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // ─── GET /api/matches ─────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> findAll() {
        return ResponseEntity.ok(matchService.findAll());
    }

    // ─── GET /api/matches/{id} ────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    // ─── GET /api/matches/match/{matchId} ─────────────────────────────────────
    @GetMapping("/match/{matchId}")
    public ResponseEntity<MatchResponseDTO> findByMatchId(@PathVariable String matchId) {
        return ResponseEntity.ok(matchService.findByMatchId(matchId));
    }

    // ─── REPORTE: GET /api/matches/result/{result} ────────────────────────────
    @GetMapping("/result/{result}")
    public ResponseEntity<List<MatchResponseDTO>> findByResult(@PathVariable String result) {
        return ResponseEntity.ok(matchService.findByResult(result));
    }

    // ─── REPORTE: GET /api/matches/mode/{gameMode} ────────────────────────────
    @GetMapping("/mode/{gameMode}")
    public ResponseEntity<List<MatchResponseDTO>> findByGameMode(@PathVariable String gameMode) {
        return ResponseEntity.ok(matchService.findByGameMode(gameMode));
    }

    // ─── REPORTE: GET /api/matches/map/{mapName} ──────────────────────────────
    @GetMapping("/map/{mapName}")
    public ResponseEntity<List<MatchResponseDTO>> findByMap(@PathVariable String mapName) {
        return ResponseEntity.ok(matchService.findByMap(mapName));
    }

    // ─── REPORTE: GET /api/matches/player/{username}/history ──────────────────
    @GetMapping("/player/{username}/history")
    public ResponseEntity<List<MatchPlayerDTO>> findPlayerHistory(@PathVariable String username) {
        return ResponseEntity.ok(matchService.findPlayersByUsername(username));
    }

    // ─── REPORTE: GET /api/matches/player/{username}/kda ──────────────────────
    @GetMapping("/player/{username}/kda")
    public ResponseEntity<KdaSummaryDTO> getPlayerKda(@PathVariable String username) {
        return ResponseEntity.ok(matchService.getPlayerKdaSummary(username));
    }

    // ─── POST /api/matches ────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<MatchResponseDTO> create(@Valid @RequestBody MatchRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(dto));
    }

    // ─── DELETE /api/matches/{id} ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
