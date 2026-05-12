package cl.hitzone.ms_weapons.exception;

// Excepción personalizada para cuando un recurso no es encontrado en la base de datos (Ej: un Arma por su ID o Nombre)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
