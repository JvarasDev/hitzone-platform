package cl.hitzone.ms_agents.service;

import cl.hitzone.ms_agents.dto.AbilityDTO;
import cl.hitzone.ms_agents.dto.AgentRequestDTO;
import cl.hitzone.ms_agents.dto.AgentResponseDTO;
import java.util.List;

public interface AgentService {
    List<AgentResponseDTO> findAll();
    AgentResponseDTO findById(Long id);
    List<AbilityDTO> getAgentAbilities(Long id);
    AgentResponseDTO create(AgentRequestDTO requestDTO);
    AgentResponseDTO update(Long id, AgentRequestDTO requestDTO);
    void delete(Long id);
}
