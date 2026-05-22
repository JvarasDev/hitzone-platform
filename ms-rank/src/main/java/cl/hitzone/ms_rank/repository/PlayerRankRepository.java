package cl.hitzone.ms_rank.repository;

import cl.hitzone.ms_rank.model.PlayerRank;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerRankRepository extends JpaRepository<PlayerRank,Long> {
    //buscar por username
    Optional<PlayerRank> findByUsername(String username);

    //Verificar si existe un username
    boolean existsByUsername(String username);

    List<PlayerRank> findAll(Sort sort);

    @Query("SELECT pr.rankName, COUNT(pr) FROM PlayerRank pr GROUP BY pr.rankName ORDER BY MIN(pr.rankNumber)")
    List<Object[]> getRankDistribution();

    List<PlayerRank> findByUsernameContainingIgnoreCase(String username, Sort sort);
}

