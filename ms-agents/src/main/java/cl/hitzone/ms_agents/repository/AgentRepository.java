package cl.hitzone.ms_agents.repository;

import cl.hitzone.ms_agents.model.Agent;
import cl.hitzone.ms_agents.model.AgentRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Agent> findByRole(AgentRole role);

    @Query("SELECT a.role, COUNT(a) FROM Agent a GROUP BY a.role")
    List<Object[]> countByRole();
}
