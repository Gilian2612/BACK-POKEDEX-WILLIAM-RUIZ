package com.wilddex.dto.response;

import java.util.List;

/**
 * Cadena evolutiva de un Pokémon (PKX-009).
 */
public record EvolutionChainResponse(
        List<EvolutionStage> stages
) {

    public record EvolutionStage(
            int pokemonId,
            String name,
            String spriteUrl,
            String evolutionTrigger,
            List<EvolutionStage> evolvesTo
    ) {
    }
}
