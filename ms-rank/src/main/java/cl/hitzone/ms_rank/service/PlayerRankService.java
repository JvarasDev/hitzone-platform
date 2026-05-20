package cl.hitzone.ms_rank.service;

import cl.hitzone.ms_rank.dto.PlayerRankRequestDTO;
import cl.hitzone.ms_rank.dto.PlayerRankResponseDTO;

import java.util.List;

public interface PlayerRankService {
    List<PlayerRankResponseDTO> getAllRanks();
    PlayerRankResponseDTO getRankByUsername(String username);
    PlayerRankResponseDTO createRank(PlayerRankRequestDTO dto);
    PlayerRankResponseDTO updateRank(String username, PlayerRankRequestDTO dto);
    void deleteRank(String username);
}
