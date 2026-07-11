package com.wilddex.dto.response;

import java.util.List;
import java.util.Map;

/**
 * Comparación lado a lado de dos Pokémon (PKX-013).
 */
public record CompareResponse(
        PokemonCompareItem pokemon1,
        PokemonCompareItem pokemon2
) {

    public record PokemonCompareItem(
            int id,
            String name,
            String spriteUrl,
            List<String> types,
            Map<String, Integer> stats,
            int totalStats
    ) {
    }
}
