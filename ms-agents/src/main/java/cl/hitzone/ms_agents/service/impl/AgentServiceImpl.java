package cl.hitzone.ms_agents.service.impl;

import cl.hitzone.ms_agents.dto.AbilityDTO;
import cl.hitzone.ms_agents.dto.AgentRequestDTO;
import cl.hitzone.ms_agents.dto.AgentResponseDTO;
import cl.hitzone.ms_agents.exception.DuplicateResourceException;
import cl.hitzone.ms_agents.exception.ResourceNotFoundException;
import cl.hitzone.ms_agents.model.Ability;
import cl.hitzone.ms_agents.model.Agent;
import cl.hitzone.ms_agents.repository.AgentRepository;
import cl.hitzone.ms_agents.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    @Override
    public List<AgentResponseDTO> findAll() {
        return agentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AgentResponseDTO findById(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agente no encontrado con ID: " + id));
        return mapToResponseDTO(agent);
    }

    @Override
    public List<AbilityDTO> getAgentAbilities(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agente no encontrado con ID: " + id));
        
        return agent.getAbilities().stream()
                .map(this::mapToAbilityDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AgentResponseDTO create(AgentRequestDTO requestDTO) {
        if (agentRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("El agente " + requestDTO.getName() + " ya existe");
        }

        Agent agent = new Agent();
        agent.setName(requestDTO.getName());
        agent.setRole(requestDTO.getRole());
        agent.setDescription(requestDTO.getDescription());
        agent.setImageUrl(requestDTO.getImageUrl());

        if (requestDTO.getAbilities() != null) {
            List<Ability> abilities = requestDTO.getAbilities().stream().map(dto -> {
                Ability ability = new Ability();
                ability.setName(dto.getName());
                ability.setType(dto.getType());
                ability.setDescription(dto.getDescription());
                ability.setAgent(agent);
                return ability;
            }).collect(Collectors.toList());
            agent.setAbilities(abilities);
        }

        return mapToResponseDTO(agentRepository.save(agent));
    }

    @Override
    public AgentResponseDTO update(Long id, AgentRequestDTO requestDTO) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agente no encontrado con ID: " + id));

        if (agentRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            throw new DuplicateResourceException("El nombre " + requestDTO.getName() + " ya está en uso");
        }

        agent.setName(requestDTO.getName());
        agent.setRole(requestDTO.getRole());
        agent.setDescription(requestDTO.getDescription());
        agent.setImageUrl(requestDTO.getImageUrl());

        if (requestDTO.getAbilities() != null) {
            agent.getAbilities().clear();
            List<Ability> abilities = requestDTO.getAbilities().stream().map(dto -> {
                Ability ability = new Ability();
                ability.setName(dto.getName());
                ability.setType(dto.getType());
                ability.setDescription(dto.getDescription());
                ability.setAgent(agent);
                return ability;
            }).collect(Collectors.toList());
            agent.getAbilities().addAll(abilities);
        }

        return mapToResponseDTO(agentRepository.save(agent));
    }

    @Override
    public void delete(Long id) {
        if (!agentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agente no encontrado con ID: " + id);
        }
        agentRepository.deleteById(id);
    }

    private AgentResponseDTO mapToResponseDTO(Agent agent) {
        AgentResponseDTO dto = new AgentResponseDTO();
        dto.setId(agent.getId());
        dto.setName(agent.getName());
        dto.setRole(agent.getRole());
        dto.setDescription(agent.getDescription());
        dto.setImageUrl(agent.getImageUrl());
        
        if (agent.getAbilities() != null) {
            List<AbilityDTO> abilityDTOs = agent.getAbilities().stream()
                    .map(this::mapToAbilityDTO)
                    .collect(Collectors.toList());
            dto.setAbilities(abilityDTOs);
        }
        return dto;
    }

    private AbilityDTO mapToAbilityDTO(Ability ability) {
        AbilityDTO dto = new AbilityDTO();
        dto.setId(ability.getId());
        dto.setName(ability.getName());
        dto.setType(ability.getType());
        dto.setDescription(ability.getDescription());
        return dto;
    }
}
