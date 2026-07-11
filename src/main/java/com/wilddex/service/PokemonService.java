package com.wilddex.service;

import com.wilddex.dto.response.*;
import com.wilddex.repository.CapturedPokemonRepository;
import com.wilddex.repository.FavoritePokemonRepository;
import com.wilddex.repository.TeamMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Pokémon: orquesta llamadas a PokeApiClient y agrega datos locales.
 * PKX-005 a PKX-009.
 */
@Service
public class PokemonService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonService.class);

    private final PokeApiClient pokeApiClient;
    private final CapturedPokemonRepository capturedPokemonRepository;
    private final FavoritePokemonRepository favoritePokemonRepository;
    private final TeamMemberRepository teamMemberRepository;

    public PokemonService(PokeApiClient pokeApiClient,
                          CapturedPokemonRepository capturedPokemonRepository,
                          FavoritePokemonRepository favoritePokemonRepository,
                          TeamMemberRepository teamMemberRepository) {
        this.pokeApiClient = pokeApiClient;
        this.capturedPokemonRepository = capturedPokemonRepository;
        this.favoritePokemonRepository = favoritePokemonRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    /** PKX-005: Listado paginado. */
    public PokemonListResponse listPokemon(int page, int size) {
        int offset = page * size;
        List<PokemonSummary> pokemon = pokeApiClient.getPokemonList(offset, size);
        long total = pokeApiClient.getTotalPokemonCount();
        int totalPages = (int) Math.ceil((double) total / size);

        return new PokemonListResponse(pokemon, page, size, totalPages, total);
    }

    /** PKX-006: Búsqueda por nombre. */
    public List<PokemonSummary> searchByName(String query) {
        return pokeApiClient.searchPokemon(query);
    }

    /** PKX-007: Filtro por tipo. */
    public List<PokemonSummary> filterByType(String type) {
        return pokeApiClient.getPokemonByType(type);
    }

    /** PKX-007: Filtro por generación. */
    public List<PokemonSummary> filterByGeneration(int generation) {
        return pokeApiClient.getPokemonByGeneration(generation);
    }

    /** PKX-008: Detalle completo. */
    public PokemonDetailResponse getDetail(String idOrName) {
        return pokeApiClient.getPokemonDetail(idOrName);
    }

    /** PKX-009: Cadena evolutiva. */
    public EvolutionChainResponse getEvolutionChain(int pokemonId) {
        return pokeApiClient.getEvolutionChain(pokemonId);
    }

    /** Estadísticas de un Pokémon (capturas, favoritos, equipos). */
    public PokemonStatsResponse getStats(int pokemonId) {
        PokemonDetailResponse detail = pokeApiClient.getPokemonDetail(String.valueOf(pokemonId));
        long capturedCount = capturedPokemonRepository.countByPokemonId(pokemonId);
        long favoritedCount = favoritePokemonRepository.countByPokemonId(pokemonId);
        long teamsCount = teamMemberRepository.countTeamsWithPokemon(pokemonId);

        return new PokemonStatsResponse(pokemonId, detail.name(), teamsCount, capturedCount, favoritedCount);
    }
}
