package cl.hitzone.ms_maps.repository;

import cl.hitzone.ms_maps.model.modelMaps;
import cl.hitzone.ms_maps.model.typeMaps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Repositorio de Maps - Comunica con la BD
 * JpaRepository proporciona métodos CRUD automáticamente
 */
public interface MapRepository extends JpaRepository<modelMaps, Long> {

    // Buscar mapas por tipo específico
    List<modelMaps> findByType(typeMaps type);

    // Buscar mapas que coincidan con una colección de tipos
    List<modelMaps> findByTypeIn(Collection<typeMaps> types);

    @Query("SELECT m.difficulty, COUNT(m) FROM modelMaps m GROUP BY m.difficulty")
    List<Object[]> countByDifficulty();
}

