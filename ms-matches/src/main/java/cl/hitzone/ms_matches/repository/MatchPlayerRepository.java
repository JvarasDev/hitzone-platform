package cl.hitzone.ms_matches.repository;


import cl.hitzone.ms_matches.model.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {
    // ── Derivados automáticos (Spring los genera solo) ──
    // ── Derivados automáticos ───────────────────────────

    List<MatchPlayer> findByUsername(String username);

    List<MatchPlayer> findByMatchId(Long matchId);

    List<MatchPlayer> findByAgentName(String agentName);

    // ── Reportes personalizados ─────────────────────────

    @Query("SELECT mp FROM MatchPlayer mp WHERE mp.username = :username " +
            "ORDER BY mp.kills DESC")
    List<MatchPlayer> findByUsernameOrderByKillsDesc(
            @Param("username") String username);

    @Query("SELECT SUM(mp.kills), SUM(mp.deaths), SUM(mp.assists) " +
            "FROM MatchPlayer mp WHERE mp.username = :username")
    Object[] getKdaSummaryByUsername(@Param("username") String username);

    @Query("SELECT mp.agentName, COUNT(mp) FROM MatchPlayer mp " +
            "WHERE mp.username = :username GROUP BY mp.agentName " +
            "ORDER BY COUNT(mp) DESC")
    List<Object[]> findMostPlayedAgentByUsername(
            @Param("username") String username);


}
