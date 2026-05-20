package cl.hitzone.ms_rank.repository;

import cl.hitzone.ms_rank.model.PlayerRank;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRankRepository extends JpaRepository<PlayerRank,Long> {
    //buscar por username
    Optional<PlayerRank> findByUsername(String username);

    //Verificar si existe un username
    boolean existsByUsername(String username);

    List<PlayerRank> findAll(Sort sort);
}
