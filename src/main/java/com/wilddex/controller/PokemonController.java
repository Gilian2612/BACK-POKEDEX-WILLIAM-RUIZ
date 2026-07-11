package com.wilddex.controller;

import com.wilddex.dto.response.*;
import com.wilddex.service.PokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de Pokémon (PKX-005 a PKX-009).
 */
@RestController
@RequestMapping("/api/v1/pokemon")
@Tag(name = "Pokémon", description = "Listado, búsqueda, filtros, detalle y evolución")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    @Operation(summary = "Listar Pokémon", description = "PKX-005: Listado paginado de Pokémon")
    public ResponseEntity<ApiResponse<PokemonListResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PokemonListResponse response = pokemonService.listPokemon(page, size);
        return ResponseEntity.ok(ApiResponse.success("Listado de Pokémon", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar Pokémon", description = "PKX-006: Buscar por nombre parcial")
    public ResponseEntity<ApiResponse<List<PokemonSummary>>> search(@RequestParam String query) {
        List<PokemonSummary> results = pokemonService.searchByName(query);
        return ResponseEntity.ok(ApiResponse.success("Resultados de búsqueda", results));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Filtrar por tipo", description = "PKX-007: Pokémon de un tipo específico")
    public ResponseEntity<ApiResponse<List<PokemonSummary>>> byType(@PathVariable String type) {
        List<PokemonSummary> results = pokemonService.filterByType(type);
        return ResponseEntity.ok(ApiResponse.success("Pokémon de tipo " + type, results));
    }

    @GetMapping("/generation/{generation}")
    @Operation(summary = "Filtrar por generación", description = "PKX-007: Pokémon de una generación")
    public ResponseEntity<ApiResponse<List<PokemonSummary>>> byGeneration(@PathVariable int generation) {
        List<PokemonSummary> results = pokemonService.filterByGeneration(generation);
        return ResponseEntity.ok(ApiResponse.success("Pokémon de generación " + generation, results));
    }

    @GetMapping("/{idOrName}")
    @Operation(summary = "Detalle de Pokémon", description = "PKX-008: Información completa de un Pokémon")
    public ResponseEntity<ApiResponse<PokemonDetailResponse>> detail(@PathVariable String idOrName) {
        PokemonDetailResponse detail = pokemonService.getDetail(idOrName);
        return ResponseEntity.ok(ApiResponse.success("Detalle de Pokémon", detail));
    }

    @GetMapping("/{id}/evolution")
    @Operation(summary = "Cadena evolutiva", description = "PKX-009: Cadena de evolución de un Pokémon")
    public ResponseEntity<ApiResponse<EvolutionChainResponse>> evolution(@PathVariable int id) {
        EvolutionChainResponse chain = pokemonService.getEvolutionChain(id);
        return ResponseEntity.ok(ApiResponse.success("Cadena evolutiva", chain));
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Estadísticas de uso", description = "Capturas, favoritos y equipos que incluyen al Pokémon")
    public ResponseEntity<ApiResponse<PokemonStatsResponse>> stats(@PathVariable int id) {
        PokemonStatsResponse stats = pokemonService.getStats(id);
        return ResponseEntity.ok(ApiResponse.success("Estadísticas del Pokémon", stats));
    }
}
