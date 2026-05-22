package cl.hitzone.ms_maps.controller;

import cl.hitzone.ms_maps.dto.MapRequestDTO;
import cl.hitzone.ms_maps.dto.MapResponseDTO;
import cl.hitzone.ms_maps.model.typeMaps;
import cl.hitzone.ms_maps.service.MapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maps")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @PostMapping
    public ResponseEntity<MapResponseDTO> createMap(@Valid @RequestBody MapRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapService.createMap(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<MapResponseDTO>> getAllMaps() {
        return ResponseEntity.ok(mapService.getAllMaps());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MapResponseDTO> getMapById(@PathVariable Long id) {
        return ResponseEntity.ok(mapService.getMapById(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MapResponseDTO>> getMapsByType(@PathVariable String tipo) {
        typeMaps tipoEnum = typeMaps.valueOf(tipo.toUpperCase());
        return ResponseEntity.ok(mapService.getMapsByType(tipoEnum));
    }

    @GetMapping("/analizar/{id}")
    public ResponseEntity<String> analyzeMap(@PathVariable Long id) {
        return ResponseEntity.ok(mapService.analyzeMap(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MapResponseDTO>> filterMaps(@RequestParam Collection<typeMaps> types) {
        return ResponseEntity.ok(mapService.filterMaps(types));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MapResponseDTO> updateMap(
            @PathVariable Long id,
            @Valid @RequestBody MapRequestDTO requestDTO) {
        return ResponseEntity.ok(mapService.updateMap(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMap(@PathVariable Long id) {
        mapService.deleteMap(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/by-difficulty")
    public ResponseEntity<Map<String, Long>> getMapsCountByDifficulty() {
        return ResponseEntity.ok(mapService.countMapsByDifficulty());
    }
}
