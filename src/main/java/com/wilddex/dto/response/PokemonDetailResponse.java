package com.wilddex.dto.response;

import java.util.List;
import java.util.Map;

/**
 * Detalle completo de un Pokémon (PKX-008).
 */
public record PokemonDetailResponse(
        int id,
        String name,
        String spriteUrl,
        String imageUrl,
        List<String> types,
        Map<String, Integer> stats,
        List<AbilityInfo> abilities,
        String description,
        int height,
        int weight,
        int generation,
        String region
) {

    public record AbilityInfo(
            String name,
            boolean isHidden
    ) {
    }
}
