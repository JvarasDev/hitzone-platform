package cl.hitzone.ms_weapons.controller;

import cl.hitzone.ms_weapons.dto.WeaponCategoryStatsDTO;
import cl.hitzone.ms_weapons.dto.WeaponRequestDTO;
import cl.hitzone.ms_weapons.dto.WeaponResponseDTO;
import cl.hitzone.ms_weapons.service.WeaponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weapons")
@RequiredArgsConstructor
public class WeaponController {

    private final WeaponService weaponService;

    // ─── GET /api/weapons ─────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<WeaponResponseDTO>> findAll() {
        return ResponseEntity.ok(weaponService.findAll());
    }

    // ─── GET /api/weapons/{id} ────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<WeaponResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(weaponService.findById(id));
    }

    // ─── REPORTE: GET /api/weapons/category/{cat} ─────────────────────────────
    @GetMapping("/category/{cat}")
    public ResponseEntity<List<WeaponResponseDTO>> findByCategory(@PathVariable String cat) {
        return ResponseEntity.ok(weaponService.findByCategory(cat));
    }

    // ─── REPORTE: GET /api/weapons/price-range?min=0&max=3000 ────────────────
    @GetMapping("/price-range")
    public ResponseEntity<List<WeaponResponseDTO>> findByPriceRange(
            @RequestParam Integer min,
            @RequestParam Integer max) {
        return ResponseEntity.ok(weaponService.findByPriceRange(min, max));
    }

    // ─── REPORTE: GET /api/weapons/cheapest ───────────────────────────────────
    @GetMapping("/cheapest")
    public ResponseEntity<List<WeaponResponseDTO>> findCheapest() {
        return ResponseEntity.ok(weaponService.findByPriceRange(0, 1000));
    }

    // ─── REPORTE: GET /api/weapons/stats/by-category ─────────────────────────
    @GetMapping("/stats/by-category")
    public ResponseEntity<List<WeaponCategoryStatsDTO>> getStatsByCategory() {
        return ResponseEntity.ok(weaponService.getStatsByCategory());
    }

    // ─── POST /api/weapons ────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<WeaponResponseDTO> create(@Valid @RequestBody WeaponRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weaponService.create(dto));
    }

    // ─── PUT /api/weapons/{id} ────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<WeaponResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody WeaponRequestDTO dto) {
        return ResponseEntity.ok(weaponService.update(id, dto));
    }

    // ─── DELETE /api/weapons/{id} ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        weaponService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
