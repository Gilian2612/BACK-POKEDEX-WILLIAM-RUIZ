package com.wilddex.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registro de usuario (PKX-001).
 * Validaciones de input en el DTO según sección 12.3 DOSW.
 */
public record RegisterRequest(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 4, message = "El nombre de usuario debe tener al menos 4 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "El nombre de usuario debe ser alfanumérico")
        String username,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El formato del correo electrónico no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
                 message = "La contraseña debe tener al menos una mayúscula, un número y un carácter especial")
        String password,

        @NotBlank(message = "La confirmación de contraseña es obligatoria")
        String confirmPassword
) {
}
