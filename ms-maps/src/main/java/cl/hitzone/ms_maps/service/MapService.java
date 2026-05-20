package cl.hitzone.ms_maps.service;

import cl.hitzone.ms_maps.dto.MapRequestDTO;
import cl.hitzone.ms_maps.dto.MapResponseDTO;
import cl.hitzone.ms_maps.model.typeMaps;

import java.util.Collection;
import java.util.List;

public interface MapService {
    MapResponseDTO createMap(MapRequestDTO requestDTO);
    List<MapResponseDTO> getAllMaps();
    MapResponseDTO getMapById(Long id);
    List<MapResponseDTO> getMapsByType(typeMaps type);
    String analyzeMap(Long id);
    List<MapResponseDTO> filterMaps(Collection<typeMaps> allowedTypes);
    MapResponseDTO updateMap(Long id, MapRequestDTO requestDTO);
    void deleteMap(Long id);
}
