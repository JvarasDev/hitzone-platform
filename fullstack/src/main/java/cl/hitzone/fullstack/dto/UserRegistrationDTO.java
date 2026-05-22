package cl.hitzone.fullstack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {
    @NotBlank(message = "El campo username no puede estar vacio.")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial")
    private String password;

    @NotBlank(message = "El campo email no puede estar vacio.")
    @Email(message = "El campo email debe tener formato de correo [@ y DOM].")
    private String email;
}
