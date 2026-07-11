package com.wilddex.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO para crear o actualizar un equipo Pokémon.
 */
public record TeamRequest(

        @NotBlank(message = "El nombre del equipo es obligatorio")
        @Size(max = 100, message = "El nombre del equipo no puede exceder 100 caracteres")
        String name,

        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String description,

        @Size(max = 6, message = "Un equipo Pokémon no puede tener más de 6 miembros")
        List<TeamMemberRequest> members
) {
}
