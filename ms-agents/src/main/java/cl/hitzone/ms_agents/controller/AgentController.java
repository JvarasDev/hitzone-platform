package cl.hitzone.ms_agents.controller;

import cl.hitzone.ms_agents.dto.AbilityDTO;
import cl.hitzone.ms_agents.dto.AgentRequestDTO;
import cl.hitzone.ms_agents.dto.AgentResponseDTO;
import cl.hitzone.ms_agents.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    public ResponseEntity<List<AgentResponseDTO>> findAll() {
        return ResponseEntity.ok(agentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.findById(id));
    }

    @GetMapping("/{id}/abilities")
    public ResponseEntity<List<AbilityDTO>> getAgentAbilities(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.getAgentAbilities(id));
    }

    @PostMapping
    public ResponseEntity<AgentResponseDTO> create(@Valid @RequestBody AgentRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agentService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AgentRequestDTO requestDTO) {
        return ResponseEntity.ok(agentService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
