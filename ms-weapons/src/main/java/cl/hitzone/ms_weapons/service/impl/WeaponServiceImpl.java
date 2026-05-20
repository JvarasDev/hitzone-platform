package cl.hitzone.ms_weapons.service.impl;

import cl.hitzone.ms_weapons.dto.WeaponCategoryStatsDTO;
import cl.hitzone.ms_weapons.dto.WeaponRequestDTO;
import cl.hitzone.ms_weapons.dto.WeaponResponseDTO;
import cl.hitzone.ms_weapons.exception.DuplicateResourceException;
import cl.hitzone.ms_weapons.exception.ResourceNotFoundException;
import cl.hitzone.ms_weapons.model.Weapon;
import cl.hitzone.ms_weapons.model.WeaponCategory;
import cl.hitzone.ms_weapons.repository.WeaponRepository;
import cl.hitzone.ms_weapons.service.WeaponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeaponServiceImpl implements WeaponService {

    private final WeaponRepository weaponRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WeaponResponseDTO> findAll() {
        return weaponRepository.findAll().stream()
                .map(WeaponResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WeaponResponseDTO findById(Long id) {
        Weapon weapon = weaponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arma no encontrada con ID: " + id));
        return new WeaponResponseDTO(weapon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeaponResponseDTO> findByCategory(String category) {
        WeaponCategory enumCategory = WeaponCategory.valueOf(category.toUpperCase());
        return weaponRepository.findByCategory(enumCategory).stream()
                .map(WeaponResponseDTO::new)
                .collect(Collectors.toList());
    }

    // ─── REPORTE: filtrar por rango de precio ─────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<WeaponResponseDTO> findByPriceRange(Integer min, Integer max) {
        return weaponRepository.findByPriceRange(min, max).stream()
                .map(WeaponResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WeaponResponseDTO create(WeaponRequestDTO dto) {
        if (weaponRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "No se puede crear el arma: El nombre '" + dto.getName() + "' ya existe.");
        }

        Weapon weapon = new Weapon();
        weapon.setName(dto.getName());
        weapon.setCategory(dto.getCategory());
        weapon.setDamage(dto.getDamage());
        weapon.setFireRate(dto.getFireRate());
        weapon.setMagazineSize(dto.getMagazineSize());
        weapon.setPrice(dto.getPrice());
        weapon.setDescription(dto.getDescription());

        return new WeaponResponseDTO(weaponRepository.save(weapon));
    }

    @Override
    @Transactional
    public WeaponResponseDTO update(Long id, WeaponRequestDTO dto) {
        Weapon existingWeapon = weaponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arma no encontrada con ID: " + id));

        if (weaponRepository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new DuplicateResourceException(
                    "No se puede actualizar: El nombre '" + dto.getName() + "' ya está siendo usado por otra arma.");
        }

        existingWeapon.setName(dto.getName());
        existingWeapon.setCategory(dto.getCategory());
        existingWeapon.setDamage(dto.getDamage());
        existingWeapon.setFireRate(dto.getFireRate());
        existingWeapon.setMagazineSize(dto.getMagazineSize());
        existingWeapon.setPrice(dto.getPrice());
        existingWeapon.setDescription(dto.getDescription());

        return new WeaponResponseDTO(weaponRepository.save(existingWeapon));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Weapon existing = weaponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arma no encontrada con ID: " + id));
        weaponRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeaponCategoryStatsDTO> getStatsByCategory() {
        return weaponRepository.countByCategory()
            .stream()
            .map(row -> WeaponCategoryStatsDTO.builder()
                .category(row[0].toString())
                .count(((Number) row[1]).longValue())
                .build())
            .collect(Collectors.toList());
    }
}
