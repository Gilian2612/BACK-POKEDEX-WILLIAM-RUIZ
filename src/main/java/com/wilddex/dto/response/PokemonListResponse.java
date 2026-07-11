package com.wilddex.dto.response;

import java.util.List;

/**
 * Respuesta paginada del listado de Pokémon (PKX-005).
 */
public record PokemonListResponse(
        List<PokemonSummary> pokemon,
        int page,
        int size,
        int totalPages,
        long totalElements
) {
}
