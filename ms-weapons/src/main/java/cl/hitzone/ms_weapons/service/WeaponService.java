package cl.hitzone.ms_weapons.service;

import cl.hitzone.ms_weapons.dto.WeaponRequestDTO;
import cl.hitzone.ms_weapons.model.Weapon;

import java.util.List;

// Interfaz que define el contrato de nuestra capa de servicio (Lógica de Negocio).
public interface WeaponService {
    
    // Obtiene todas las armas registradas
    List<Weapon> findAll();
    
    // Obtiene una sola arma buscando por su Primary Key (ID)
    Weapon findById(Long id);
    
    // Filtra las armas según su categoría
    List<Weapon> findByCategory(String category);
    
    // Valida y crea una nueva arma
    Weapon create(WeaponRequestDTO dto);
    
    // Valida y actualiza los datos de un arma existente
    Weapon update(Long id, WeaponRequestDTO dto);
    
    // Elimina de la base de datos un arma existente
    void delete(Long id);
}
