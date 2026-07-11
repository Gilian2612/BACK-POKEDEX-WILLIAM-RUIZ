package com.wilddex.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TeamMemberRequest(

        @NotNull(message = "El ID del Pokémon es obligatorio")
        Integer pokemonId,

        @NotNull(message = "El nombre del Pokémon es obligatorio")
        String pokemonName,

        @NotNull(message = "El slot es obligatorio")
        @Min(value = 1, message = "El slot mínimo es 1")
        @Max(value = 6, message = "El slot máximo es 6")
        Integer slot
) {
}
