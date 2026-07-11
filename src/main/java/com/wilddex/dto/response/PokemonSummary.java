package com.wilddex.dto.response;

import java.util.List;

/**
 * Resumen de un Pokémon para listado (número, nombre, sprite, tipos).
 */
public record PokemonSummary(
        int id,
        String name,
        String spriteUrl,
        List<String> types
) {
}
