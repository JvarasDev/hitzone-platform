package cl.hitzone.ms_weapons.controller;

import cl.hitzone.ms_weapons.dto.WeaponRequestDTO;
import cl.hitzone.ms_weapons.model.Weapon;
import cl.hitzone.ms_weapons.service.WeaponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: Indica que es un controlador REST. Spring convertirá automáticamente
// los objetos devueltos en formato JSON para la respuesta HTTP.
// @RequestMapping: Define la ruta base para todos los métodos (endpoints) de esta clase.
@RestController
@RequestMapping("/api/weapons")
@RequiredArgsConstructor // Lombok genera automáticamente el constructor para inyectar 'weaponService'
public class WeaponController {

    // Dependencia de la capa de negocio (Servicio)
    private final WeaponService weaponService;

    // Endpoint para obtener todas las armas registradas.
    // Método HTTP: GET | Endpoint: /api/weapons
    @GetMapping
    public ResponseEntity<List<Weapon>> findAll() {
        // Devuelve HTTP 200 (OK) con la lista de armas
        return ResponseEntity.ok(weaponService.findAll());
    }

    // Endpoint para buscar un arma por su ID numérico.
    // Método HTTP: GET | Endpoint: /api/weapons/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Weapon> findById(@PathVariable Long id) {
        // Si no la encuentra, el servicio lanza la excepción y el GlobalHandler responde con 404.
        // Si la encuentra, devuelve HTTP 200 (OK) con el arma.
        return ResponseEntity.ok(weaponService.findById(id));
    }

    // Endpoint para buscar la lista de armas pertenecientes a una categoría específica.
    // Método HTTP: GET | Endpoint: /api/weapons/category/{cat}
    @GetMapping("/category/{cat}")
    public ResponseEntity<List<Weapon>> findByCategory(@PathVariable String cat) {
        return ResponseEntity.ok(weaponService.findByCategory(cat));
    }

    // Endpoint para crear una nueva arma.
    // @Valid: Forza a Spring a revisar las reglas del DTO (@NotNull, @Min, etc.) antes de ejecutar el código.
    // @RequestBody: Parsea el JSON que envía el cliente al objeto WeaponRequestDTO.
    // Método HTTP: POST | Endpoint: /api/weapons
    @PostMapping
    public ResponseEntity<Weapon> create(@Valid @RequestBody WeaponRequestDTO dto) {
        Weapon newWeapon = weaponService.create(dto);
        // Exigencia de la API REST: Cuando se crea algo, se devuelve un status HTTP 201 CREATED.
        return new ResponseEntity<>(newWeapon, HttpStatus.CREATED);
    }

    // Endpoint para actualizar todos los campos de un arma según su ID.
    // También usa @Valid para proteger los datos entrantes.
    // Método HTTP: PUT | Endpoint: /api/weapons/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Weapon> update(@PathVariable Long id, @Valid @RequestBody WeaponRequestDTO dto) {
        Weapon updatedWeapon = weaponService.update(id, dto);
        return ResponseEntity.ok(updatedWeapon);
    }

    // Endpoint para eliminar un arma por su ID.
    // Método HTTP: DELETE | Endpoint: /api/weapons/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        weaponService.delete(id);
        // Exigencia de la API REST: Retorna status HTTP 204 NO_CONTENT, lo que significa
        // que la operación fue un éxito, pero el servidor no va a devolver ningún contenido JSON en el Body.
        return ResponseEntity.noContent().build();
    }
}
