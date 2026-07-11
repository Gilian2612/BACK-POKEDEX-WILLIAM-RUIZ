package com.wilddex.controller;

import com.wilddex.dto.response.*;
import com.wilddex.service.PokemonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonControllerTest {

    @Mock private PokemonService pokemonService;
    @InjectMocks private PokemonController pokemonController;

    @Test
    void list_shouldReturnPaginated() {
        PokemonListResponse listResp = new PokemonListResponse(List.of(), 0, 20, 1, 10);
        when(pokemonService.listPokemon(0, 20)).thenReturn(listResp);

        ResponseEntity<ApiResponse<PokemonListResponse>> response = pokemonController.list(0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().data().totalElements());
    }

    @Test
    void search_shouldReturnResults() {
        when(pokemonService.searchByName("pika")).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<PokemonSummary>>> response = pokemonController.search("pika");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(pokemonService).searchByName("pika");
    }

    @Test
    void byType_shouldReturnFiltered() {
        when(pokemonService.filterByType("fire")).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<PokemonSummary>>> response = pokemonController.byType("fire");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void byGeneration_shouldReturnFiltered() {
        when(pokemonService.filterByGeneration(1)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<PokemonSummary>>> response = pokemonController.byGeneration(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void detail_shouldReturnDetail() {
        PokemonDetailResponse detail = new PokemonDetailResponse(25, "pikachu", "s", "i",
                List.of("electric"), Map.of(), List.of(), "desc", 4, 60, 1, "kanto");
        when(pokemonService.getDetail("pikachu")).thenReturn(detail);

        ResponseEntity<ApiResponse<PokemonDetailResponse>> response = pokemonController.detail("pikachu");

        assertEquals("pikachu", response.getBody().data().name());
    }

    @Test
    void evolution_shouldReturnChain() {
        EvolutionChainResponse chain = new EvolutionChainResponse(List.of());
        when(pokemonService.getEvolutionChain(25)).thenReturn(chain);

        ResponseEntity<ApiResponse<EvolutionChainResponse>> response = pokemonController.evolution(25);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void stats_shouldReturnStats() {
        PokemonStatsResponse stats = new PokemonStatsResponse(25, "pikachu", 3, 10, 5);
        when(pokemonService.getStats(25)).thenReturn(stats);

        ResponseEntity<ApiResponse<PokemonStatsResponse>> response = pokemonController.stats(25);

        assertEquals("pikachu", response.getBody().data().pokemonName());
    }
}