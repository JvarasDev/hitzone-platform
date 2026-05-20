package cl.hitzone.ms_matches.repository;

import cl.hitzone.ms_matches.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByMatchId(String matchId);

    List<Match> findByResult(String result);

    List<Match> findByGameMode(String gameMode);

    List<Match> findByMapName(String mapName);

    List<Match> findByPlayedAtBetween(
            LocalDateTime from, LocalDateTime to);

    // ── Consultas JPQL personalizadas (reportes) ────────

    @Query("SELECT m FROM Match m ORDER BY m.playedAt DESC")
    List<Match> findAllOrderedByDate();

    @Query("SELECT m FROM Match m WHERE m.result = :result " +
            "ORDER BY m.playedAt DESC")
    List<Match> findByResultOrderedByDate(@Param("result") String result);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.result = :result")
    Long countByResult(@Param("result") String result);
}
