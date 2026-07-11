package com.wilddex.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualización de perfil (PKX-004).
 * Todos los campos son opcionales (PATCH parcial).
 */
public record UpdateProfileRequest(

        @Size(min = 4, message = "El nombre de usuario debe tener al menos 4 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "El nombre de usuario debe ser alfanumérico")
        String username,

        String profileImageUrl,

        @Size(min = 8, message = "La contraseña actual debe tener al menos 8 caracteres")
        String currentPassword,

        @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
                 message = "La nueva contraseña debe tener al menos una mayúscula, un número y un carácter especial")
        String newPassword
) {
}
