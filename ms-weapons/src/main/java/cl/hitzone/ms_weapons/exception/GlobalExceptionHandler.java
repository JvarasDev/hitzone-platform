package cl.hitzone.ms_weapons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice le dice a Spring que esta clase actuará como un interceptor global de excepciones
// para todos los controladores (@RestController). Esto centraliza el manejo de errores.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Maneja casos donde no encontramos un arma.
    // @ExceptionHandler se dispara automáticamente cuando un controlador lanza ResourceNotFoundException.
    // Retorna status HTTP 404 NOT_FOUND.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 2. Maneja casos de colisión de datos, como intentar crear un arma con un nombre que ya existe.
    // Retorna status HTTP 409 CONFLICT.
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateResourceException(DuplicateResourceException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 3. Maneja los errores de validación (@Valid) provenientes de los DTOs.
    // Se dispara cuando falla alguna regla como @NotNull o @Min.
    // Retorna status HTTP 400 BAD_REQUEST y un mapa de los campos con sus respectivos errores.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Iteramos por todos los errores recolectados por Spring Validation
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // Nombre de la propiedad en el DTO (ej: 'price')
            String errorMessage = error.getDefaultMessage();    // El mensaje de error (ej: 'Price must be at least 0')
            errors.put(fieldName, errorMessage);
        });
        
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 4. Captura cualquier otra excepción no manejada específicamente arriba (fallback).
    // Evita que la aplicación crashee mostrando la traza de error en crudo al cliente.
    // Retorna status HTTP 500 INTERNAL_SERVER_ERROR.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Ha ocurrido un error interno en el servidor: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
