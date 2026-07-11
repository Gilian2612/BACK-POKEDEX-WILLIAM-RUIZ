package com.wilddex.controller;

import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.response.CompareResponse;
import com.wilddex.service.CompareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de comparación de Pokémon (PKX-013).
 */
@RestController
@RequestMapping("/api/v1/pokemon/compare")
@Tag(name = "Comparación", description = "Comparación lado a lado de dos Pokémon")
public class CompareController {

    private final CompareService compareService;

    public CompareController(CompareService compareService) {
        this.compareService = compareService;
    }

    @GetMapping
    @Operation(summary = "Comparar Pokémon", description = "PKX-013: Comparación de stats entre dos Pokémon")
    public ResponseEntity<ApiResponse<CompareResponse>> compare(
            @RequestParam String pokemon1,
            @RequestParam String pokemon2) {
        CompareResponse response = compareService.compare(pokemon1, pokemon2);
        return ResponseEntity.ok(ApiResponse.success("Comparación de Pokémon", response));
    }
}
