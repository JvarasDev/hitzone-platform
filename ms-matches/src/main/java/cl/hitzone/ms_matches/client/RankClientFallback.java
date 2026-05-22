package cl.hitzone.ms_matches.client;

import cl.hitzone.ms_matches.dto.PlayerRankResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RankClientFallback implements RankClient {

    @Override
    public PlayerRankResponseDTO getPlayerRank(String username, String token) {
        log.warn("ms-rank no está disponible. Retornando rank por defecto para usuario: {}", username);
        PlayerRankResponseDTO fallback = new PlayerRankResponseDTO();
        fallback.setUsername(username);
        fallback.setRankName("UNRANKED");
        fallback.setRankNumber(0);
        fallback.setRrPoints(0);
        fallback.setWins(0);
        fallback.setLosses(0);
        return fallback;
    }
}
