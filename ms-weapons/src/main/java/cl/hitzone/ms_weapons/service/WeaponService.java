package cl.hitzone.ms_weapons.service;

import cl.hitzone.ms_weapons.dto.WeaponCategoryStatsDTO;
import cl.hitzone.ms_weapons.dto.WeaponRequestDTO;
import cl.hitzone.ms_weapons.dto.WeaponResponseDTO;

import java.util.List;

public interface WeaponService {

    List<WeaponResponseDTO> findAll();
    WeaponResponseDTO findById(Long id);
    List<WeaponResponseDTO> findByCategory(String category);
    List<WeaponResponseDTO> findByPriceRange(Integer min, Integer max);
    WeaponResponseDTO create(WeaponRequestDTO dto);
    WeaponResponseDTO update(Long id, WeaponRequestDTO dto);
    void delete(Long id);
    
    List<WeaponCategoryStatsDTO> getStatsByCategory();
}
