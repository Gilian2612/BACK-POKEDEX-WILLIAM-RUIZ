package com.wilddex.service;

import com.wilddex.dto.response.*;
import com.wilddex.repository.CapturedPokemonRepository;
import com.wilddex.repository.FavoritePokemonRepository;
import com.wilddex.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock private PokeApiClient pokeApiClient;
    @Mock private CapturedPokemonRepository capturedPokemonRepository;
    @Mock private FavoritePokemonRepository favoritePokemonRepository;
    @Mock private TeamMemberRepository teamMemberRepository;

    @InjectMocks private PokemonService pokemonService;

    private PokemonSummary makeSummary(int id, String name) {
        return new PokemonSummary(id, name, "https://img.com/" + id + ".png", List.of("electric"));
    }

    private PokemonDetailResponse makeDetail(int id, String name) {
        return new PokemonDetailResponse(id, name, "sprite.png", "image.png",
                List.of("electric"), Map.of("hp", 35), List.of(), "A Pokemon", 4, 60, 1, "kanto");
    }

    @Test
    void listPokemon_shouldReturnPaginatedResult() {
        when(pokeApiClient.getPokemonList(0, 10)).thenReturn(List.of(makeSummary(25, "pikachu")));
        when(pokeApiClient.getTotalPokemonCount()).thenReturn(150L);

        PokemonListResponse result = pokemonService.listPokemon(0, 10);

        assertEquals(1, result.pokemon().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(15, result.totalPages());
        assertEquals(150, result.totalElements());
    }

    @Test
    void listPokemon_shouldRoundUpPages() {
        when(pokeApiClient.getPokemonList(20, 20)).thenReturn(List.of());
        when(pokeApiClient.getTotalPokemonCount()).thenReturn(55L);

        PokemonListResponse result = pokemonService.listPokemon(1, 20);

        assertEquals(3, result.totalPages());
    }

    @Test
    void searchByName_shouldDelegate() {
        List<PokemonSummary> expected = List.of(makeSummary(25, "pikachu"));
        when(pokeApiClient.searchPokemon("pika")).thenReturn(expected);

        assertEquals(expected, pokemonService.searchByName("pika"));
        verify(pokeApiClient).searchPokemon("pika");
    }

    @Test
    void filterByType_shouldDelegate() {
        List<PokemonSummary> expected = List.of(makeSummary(4, "charmander"));
        when(pokeApiClient.getPokemonByType("fire")).thenReturn(expected);

        assertEquals(expected, pokemonService.filterByType("fire"));
    }

    @Test
    void filterByGeneration_shouldDelegate() {
        List<PokemonSummary> expected = List.of(makeSummary(1, "bulbasaur"));
        when(pokeApiClient.getPokemonByGeneration(1)).thenReturn(expected);

        assertEquals(expected, pokemonService.filterByGeneration(1));
    }

    @Test
    void getDetail_shouldDelegate() {
        PokemonDetailResponse detail = makeDetail(25, "pikachu");
        when(pokeApiClient.getPokemonDetail("pikachu")).thenReturn(detail);

        assertEquals("pikachu", pokemonService.getDetail("pikachu").name());
    }

    @Test
    void getEvolutionChain_shouldDelegate() {
        EvolutionChainResponse chain = new EvolutionChainResponse(List.of(
                new EvolutionChainResponse.EvolutionStage(172, "pichu", "url", "friendship", List.of())
        ));
        when(pokeApiClient.getEvolutionChain(25)).thenReturn(chain);

        EvolutionChainResponse result = pokemonService.getEvolutionChain(25);

        assertEquals(1, result.stages().size());
        assertEquals("pichu", result.stages().get(0).name());
    }

    @Test
    void getStats_shouldAggregateFromAllSources() {
        when(pokeApiClient.getPokemonDetail("25")).thenReturn(makeDetail(25, "pikachu"));
        when(capturedPokemonRepository.countByPokemonId(25)).thenReturn(10L);
        when(favoritePokemonRepository.countByPokemonId(25)).thenReturn(5L);
        when(teamMemberRepository.countTeamsWithPokemon(25)).thenReturn(3L);

        PokemonStatsResponse result = pokemonService.getStats(25);

        assertEquals(25, result.pokemonId());
        assertEquals("pikachu", result.pokemonName());
        assertEquals(3, result.teamsCount());
        assertEquals(10, result.capturedCount());
        assertEquals(5, result.favoritedCount());
    }
}