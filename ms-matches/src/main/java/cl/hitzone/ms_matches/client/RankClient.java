package cl.hitzone.ms_matches.client;

import cl.hitzone.ms_matches.dto.PlayerRankResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ms-rank", path = "/api/v1/rank")
public interface RankClient {
    
    @GetMapping("/{username}")
    PlayerRankResponseDTO getPlayerRank(@PathVariable("username") String username, @RequestHeader("Authorization") String token);
}
