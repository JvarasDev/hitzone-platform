package cl.hitzone.ms_weapons.exception;

// Excepción personalizada para cuando se intenta crear o actualizar un recurso que ya existe o rompe una restricción de unicidad (Ej: Nombre de arma duplicado)
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
