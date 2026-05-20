package cl.hitzone.ms_maps.service;

import cl.hitzone.ms_maps.dto.MapRequestDTO;
import cl.hitzone.ms_maps.dto.MapResponseDTO;
import cl.hitzone.ms_maps.model.modelMaps;
import cl.hitzone.ms_maps.model.typeMaps;
import cl.hitzone.ms_maps.repository.MapRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import cl.hitzone.ms_maps.exception.ResourceNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapServiceImpl implements MapService {
    
    private final MapRepository mapRepository;

    public MapServiceImpl(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    @Override
    @Transactional
    public MapResponseDTO createMap(MapRequestDTO requestDTO) {
        modelMaps nuevoMapa = new modelMaps();
        nuevoMapa.setName(requestDTO.getName());
        nuevoMapa.setType(requestDTO.getType());
        nuevoMapa.setDifficulty(requestDTO.getDifficulty());
        nuevoMapa.setDescription(requestDTO.getDescription());
        nuevoMapa.setTotalSite(requestDTO.getTotalSite());

        return mapToResponseDTO(mapRepository.save(nuevoMapa));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MapResponseDTO> getAllMaps() {
        return mapRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MapResponseDTO getMapById(Long id) {
        modelMaps mapa = mapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapa con ID " + id + " no encontrado"));
        return mapToResponseDTO(mapa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MapResponseDTO> getMapsByType(typeMaps type) {
        return mapRepository.findByType(type).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String analyzeMap(Long id) {
        modelMaps mapa = mapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapa no encontrado"));

        // Switch mejorado (Java 14+) - Expresión switch
        // Java obliga a cubrir todos los valores del enum, previniendo errores en compilación
        return switch (mapa.getType()) {
            case COMPETITIVE -> "Cargando mapa competitivo con análisis de jugabilidad...";
            case UNRATED -> "Cargando mapa sin clasificación...";
            case CUSTOM -> "Cargando mapa personalizado...";
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<MapResponseDTO> filterMaps(Collection<typeMaps> allowedTypes) {
        // Si no se especifica filtro, se retornan todos los mapas disponibles
        if (allowedTypes == null || allowedTypes.isEmpty()) {
            return mapRepository.findAll().stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        }

        return mapRepository.findByTypeIn(allowedTypes).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MapResponseDTO updateMap(Long id, MapRequestDTO requestDTO) {
        modelMaps mapActual = mapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapa con ID " + id + " no encontrado"));

        // Soporte para actualizaciones parciales
        if (requestDTO.getName() != null) {
            mapActual.setName(requestDTO.getName());
        }
        if (requestDTO.getType() != null) {
            mapActual.setType(requestDTO.getType());
        }
        if (requestDTO.getDifficulty() != null) {
            mapActual.setDifficulty(requestDTO.getDifficulty());
        }
        if (requestDTO.getDescription() != null) {
            mapActual.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getTotalSite() != null) {
            mapActual.setTotalSite(requestDTO.getTotalSite());
        }

        return mapToResponseDTO(mapRepository.save(mapActual));
    }

    @Override
    @Transactional
    public void deleteMap(Long id) {
        modelMaps mapa = mapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapa con ID " + id + " no encontrado"));
        mapRepository.delete(mapa);
    }

    private MapResponseDTO mapToResponseDTO(modelMaps mapa) {
        return new MapResponseDTO(
                mapa.getId(),
                mapa.getName(),
                mapa.getType(),
                mapa.getDifficulty(),
                mapa.getDescription(),
                mapa.getTotalSite(),
                mapa.isActive()
        );
    }
}
