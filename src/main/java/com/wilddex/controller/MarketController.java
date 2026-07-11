package com.wilddex.controller;

import com.wilddex.dto.market.CreateListingRequest;
import com.wilddex.dto.market.MarketListingResponse;
import com.wilddex.dto.market.PurchaseResponse;
import com.wilddex.dto.response.ApiResponse;
import com.wilddex.security.CustomUserDetails;
import com.wilddex.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@Tag(name = "Mercado", description = "Compra y venta de Pokémon entre entrenadores")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @PostMapping("/publish")
    @Operation(summary = "Publicar Pokémon en el mercado",
            description = "Publica un Pokémon capturado para venderlo a otros entrenadores")
    public ResponseEntity<ApiResponse<MarketListingResponse>> publish(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateListingRequest request) {
        MarketListingResponse response = marketService.publish(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Pokémon publicado en el mercado", response));
    }

    @GetMapping
    @Operation(summary = "Explorar el mercado",
            description = "Lista las publicaciones activas con paginación y búsqueda opcional por nombre")
    public ResponseEntity<ApiResponse<Page<MarketListingResponse>>> explore(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<MarketListingResponse> listings = marketService.explore(search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Publicaciones del mercado", listings));
    }

    @PostMapping("/{listingId}/buy")
    @Operation(summary = "Comprar Pokémon del mercado",
            description = "Compra un Pokémon publicado pagando con monedas")
    public ResponseEntity<ApiResponse<PurchaseResponse>> buy(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long listingId) {
        PurchaseResponse response = marketService.buy(user.getId(), listingId);
        return ResponseEntity.ok(ApiResponse.success("Compra realizada exitosamente", response));
    }

    @DeleteMapping("/{listingId}")
    @Operation(summary = "Cancelar publicación",
            description = "Cancela tu publicación y devuelve el Pokémon a tu colección")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long listingId) {
        marketService.cancel(user.getId(), listingId);
        return ResponseEntity.ok(ApiResponse.ok("Publicación cancelada"));
    }

    @GetMapping("/my-listings")
    @Operation(summary = "Mis publicaciones activas",
            description = "Lista los Pokémon que tienes publicados en el mercado")
    public ResponseEntity<ApiResponse<List<MarketListingResponse>>> myListings(
            @AuthenticationPrincipal CustomUserDetails user) {
        List<MarketListingResponse> listings = marketService.myListings(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Tus publicaciones", listings));
    }

    @GetMapping("/my-purchases")
    @Operation(summary = "Mi historial de compras",
            description = "Lista los Pokémon que has comprado en el mercado")
    public ResponseEntity<ApiResponse<List<MarketListingResponse>>> myPurchases(
            @AuthenticationPrincipal CustomUserDetails user) {
        List<MarketListingResponse> purchases = marketService.myPurchases(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Tus compras", purchases));
    }
}
