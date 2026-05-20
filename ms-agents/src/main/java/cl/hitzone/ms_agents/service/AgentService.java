package cl.hitzone.ms_agents.service;

import cl.hitzone.ms_agents.dto.AbilityDTO;
import cl.hitzone.ms_agents.dto.AgentRequestDTO;
import cl.hitzone.ms_agents.dto.AgentResponseDTO;
import cl.hitzone.ms_agents.model.AgentRole;

import java.util.List;
import java.util.Map;

public interface AgentService {
    List<AgentResponseDTO> findAll();
    AgentResponseDTO findById(Long id);
    List<AbilityDTO> getAgentAbilities(Long id);
    AgentResponseDTO create(AgentRequestDTO requestDTO);
    AgentResponseDTO update(Long id, AgentRequestDTO requestDTO);
    void delete(Long id);
    
    List<AgentResponseDTO> getAgentsByRole(AgentRole role);
    Map<AgentRole, Long> getAgentCountByRole();
}
