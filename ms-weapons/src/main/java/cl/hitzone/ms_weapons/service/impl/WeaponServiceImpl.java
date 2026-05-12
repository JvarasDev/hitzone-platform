package cl.hitzone.ms_weapons.service.impl;

import cl.hitzone.ms_weapons.dto.WeaponRequestDTO;
import cl.hitzone.ms_weapons.exception.DuplicateResourceException;
import cl.hitzone.ms_weapons.exception.ResourceNotFoundException;
import cl.hitzone.ms_weapons.model.Weapon;
import cl.hitzone.ms_weapons.model.WeaponCategory;
import cl.hitzone.ms_weapons.repository.WeaponRepository;
import cl.hitzone.ms_weapons.service.WeaponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service le dice a Spring que registre esta clase como un Bean de servicio.
// @RequiredArgsConstructor (de Lombok) genera un constructor con todos los atributos 'final',
// logrando así inyectar el WeaponRepository automáticamente sin necesidad de usar @Autowired.
@Service
@RequiredArgsConstructor 
public class WeaponServiceImpl implements WeaponService {

    private final WeaponRepository weaponRepository;

    // Retorna la lista completa de armas mediante el método nativo de JpaRepository
    @Override
    public List<Weapon> findAll() {
        return weaponRepository.findAll();
    }

    // Busca el arma y usa Optional.orElseThrow.
    // Si no encuentra el arma en DB, lanza la excepción personalizada ResourceNotFoundException,
    // que a su vez será interceptada por el GlobalExceptionHandler devolviendo HTTP 404 al cliente.
    @Override
    public Weapon findById(Long id) {
        return weaponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Weapon not found"));
    }

    // Busca las armas de una categoría en específico. Convierte el String recibido en el Enum respectivo.
    @Override
    public List<Weapon> findByCategory(String category) {
        // Convertimos a mayúsculas para emparejar con el formato estándar de los Enums (ej: RIFLE)
        WeaponCategory enumCategory = WeaponCategory.valueOf(category.toUpperCase());
        return weaponRepository.findByCategory(enumCategory);
    }

    // Creación de Arma aplicando reglas de negocio estrictas.
    @Override
    public Weapon create(WeaponRequestDTO dto) {
        // Regla 1: Validar colisión de nombre. Si ya existe, lanzamos DuplicateResourceException -> HTTP 409
        if (weaponRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("No se puede crear el arma: El nombre '" + dto.getName() + "' ya existe.");
        }

        // Mapeo manual desde el DTO hacia la Entidad (también se podría usar MapStruct).
        Weapon weapon = new Weapon();
        weapon.setName(dto.getName());
        weapon.setCategory(dto.getCategory());
        weapon.setDamage(dto.getDamage());
        weapon.setFireRate(dto.getFireRate());
        weapon.setMagazineSize(dto.getMagazineSize());
        weapon.setPrice(dto.getPrice());
        weapon.setDescription(dto.getDescription());

        // Guardamos en Base de Datos y retornamos la Entidad ya guardada (con su nuevo ID y timestamps)
        return weaponRepository.save(weapon);
    }

    // Actualización de Arma, también con reglas de negocio.
    @Override
    public Weapon update(Long id, WeaponRequestDTO dto) {
        // 1. Buscamos el arma actual. Si no existe, revienta automáticamente con HTTP 404
        Weapon existingWeapon = findById(id);

        // 2. Regla de Negocio Crítica: Revisar si el "nuevo nombre" ya está ocupado.
        // Ojo: Se usa existsByNameAndIdNot para evitar que choque con su *propio nombre* si decidiste no cambiar el nombre.
        if (weaponRepository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new DuplicateResourceException("No se puede actualizar: El nombre '" + dto.getName() + "' ya está siendo usado por otra arma.");
        }

        // 3. Mapeamos las actualizaciones sobre la entidad existente (que Hibernate ya está vigilando en la sesión)
        existingWeapon.setName(dto.getName());
        existingWeapon.setCategory(dto.getCategory());
        existingWeapon.setDamage(dto.getDamage());
        existingWeapon.setFireRate(dto.getFireRate());
        existingWeapon.setMagazineSize(dto.getMagazineSize());
        existingWeapon.setPrice(dto.getPrice());
        existingWeapon.setDescription(dto.getDescription());

        // 4. Guardamos los cambios
        return weaponRepository.save(existingWeapon);
    }

    // Eliminar. 
    // Primero nos aseguramos de que existe (reutilizando findById, que arrojará 404 si no)
    // Luego se procede a su eliminación mediante el repositorio.
    @Override
    public void delete(Long id) {
        Weapon existingWeapon = findById(id);
        weaponRepository.delete(existingWeapon);
    }
}
