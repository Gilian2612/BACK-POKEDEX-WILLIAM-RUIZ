package com.wilddex.service;

import com.wilddex.dto.response.CompareResponse;
import com.wilddex.dto.response.PokemonDetailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio de comparación de Pokémon lado a lado (PKX-013).
 */
@Service
public class CompareService {

    private static final Logger logger = LoggerFactory.getLogger(CompareService.class);

    private final PokeApiClient pokeApiClient;

    public CompareService(PokeApiClient pokeApiClient) {
        this.pokeApiClient = pokeApiClient;
    }

    public CompareResponse compare(String pokemon1, String pokemon2) {
        logger.info("Comparando {} vs {}", pokemon1, pokemon2);

        PokemonDetailResponse detail1 = pokeApiClient.getPokemonDetail(pokemon1);
        PokemonDetailResponse detail2 = pokeApiClient.getPokemonDetail(pokemon2);

        int totalStats1 = detail1.stats().values().stream().mapToInt(Integer::intValue).sum();
        int totalStats2 = detail2.stats().values().stream().mapToInt(Integer::intValue).sum();

        CompareResponse.PokemonCompareItem item1 = new CompareResponse.PokemonCompareItem(
                detail1.id(), detail1.name(), detail1.spriteUrl(),
                detail1.types(), detail1.stats(), totalStats1);

        CompareResponse.PokemonCompareItem item2 = new CompareResponse.PokemonCompareItem(
                detail2.id(), detail2.name(), detail2.spriteUrl(),
                detail2.types(), detail2.stats(), totalStats2);

        return new CompareResponse(item1, item2);
    }
}
