package com.wilddex.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta de equipo Pokémon con sus miembros.
 */
public record TeamResponse(
        Long id,
        String name,
        String description,
        List<TeamMemberResponse> members,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public record TeamMemberResponse(
            Long id,
            int pokemonId,
            String pokemonName,
            int slot
    ) {
    }
}
