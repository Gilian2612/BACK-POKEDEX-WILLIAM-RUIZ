package com.wilddex.dto.response;

/**
 * Estadísticas de un Pokémon: tasa de elección en equipos, cantidad de consultas (PKX requerimiento extra).
 */
public record PokemonStatsResponse(
        int pokemonId,
        String pokemonName,
        long teamsCount,
        long capturedCount,
        long favoritedCount
) {
}
