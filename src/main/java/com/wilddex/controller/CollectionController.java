package com.wilddex.controller;

import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.response.CollectionItemResponse;
import com.wilddex.security.CustomUserDetails;
import com.wilddex.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de colección personal (PKX-010, PKX-011, PKX-012).
 */
@RestController
@RequestMapping("/api/v1/collection")
@Tag(name = "Colección", description = "Pokémon capturados y favoritos del usuario")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    // ── Capturas ──

    @GetMapping("/captured")
    @Operation(summary = "Listar capturados", description = "PKX-010: Pokémon capturados del usuario")
    public ResponseEntity<ApiResponse<List<CollectionItemResponse>>> getCaptured(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CollectionItemResponse> captured = collectionService.getCapturedPokemon(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Pokémon capturados", captured));
    }

    @PostMapping("/captured")
    @Operation(summary = "Capturar Pokémon", description = "PKX-010: Agregar Pokémon a capturas")
    public ResponseEntity<ApiResponse<CollectionItemResponse>> capture(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int pokemonId,
            @RequestParam String pokemonName) {
        CollectionItemResponse item = collectionService.capturePokemon(userDetails.getId(), pokemonId, pokemonName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("CREATED", "Pokémon capturado exitosamente", item));
    }

    @DeleteMapping("/captured/{pokemonId}")
    @Operation(summary = "Liberar Pokémon", description = "PKX-010: Eliminar de capturas")
    public ResponseEntity<ApiResponse<Void>> release(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable int pokemonId) {
        collectionService.releasePokemon(userDetails.getId(), pokemonId);
        return ResponseEntity.ok(ApiResponse.ok("Pokémon liberado"));
    }

    // ── Favoritos ──

    @GetMapping("/favorites")
    @Operation(summary = "Listar favoritos", description = "PKX-011: Pokémon favoritos del usuario")
    public ResponseEntity<ApiResponse<List<CollectionItemResponse>>> getFavorites(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CollectionItemResponse> favorites = collectionService.getFavoritePokemon(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Pokémon favoritos", favorites));
    }

    @PostMapping("/favorites")
    @Operation(summary = "Agregar favorito", description = "PKX-011: Marcar Pokémon como favorito")
    public ResponseEntity<ApiResponse<CollectionItemResponse>> addFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int pokemonId,
            @RequestParam String pokemonName) {
        CollectionItemResponse item = collectionService.addFavorite(userDetails.getId(), pokemonId, pokemonName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("CREATED", "Pokémon agregado a favoritos", item));
    }

    @DeleteMapping("/favorites/{pokemonId}")
    @Operation(summary = "Quitar favorito", description = "PKX-011: Remover de favoritos")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable int pokemonId) {
        collectionService.removeFavorite(userDetails.getId(), pokemonId);
        return ResponseEntity.ok(ApiResponse.ok("Pokémon removido de favoritos"));
    }
}
