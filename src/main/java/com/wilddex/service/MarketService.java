package com.wilddex.service;

import com.wilddex.dto.market.CreateListingRequest;
import com.wilddex.dto.market.MarketListingResponse;
import com.wilddex.dto.market.PurchaseResponse;
import com.wilddex.exception.BadRequestException;
import com.wilddex.exception.ConflictException;
import com.wilddex.exception.ForbiddenException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.model.CapturedPokemon;
import com.wilddex.model.ListingStatus;
import com.wilddex.model.MarketListing;
import com.wilddex.model.User;
import com.wilddex.repository.CapturedPokemonRepository;
import com.wilddex.repository.MarketListingRepository;
import com.wilddex.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarketService {

    private static final Logger log = LoggerFactory.getLogger(MarketService.class);

    private final MarketListingRepository marketListingRepository;
    private final CapturedPokemonRepository capturedPokemonRepository;
    private final UserRepository userRepository;

    public MarketService(MarketListingRepository marketListingRepository,
                         CapturedPokemonRepository capturedPokemonRepository,
                         UserRepository userRepository) {
        this.marketListingRepository = marketListingRepository;
        this.capturedPokemonRepository = capturedPokemonRepository;
        this.userRepository = userRepository;
    }

    /**
     * Publicar un Pokémon capturado en el mercado.
     * El vendedor define el precio. El Pokémon debe estar en su colección.
     */
    @Transactional
    public MarketListingResponse publish(Long userId, CreateListingRequest request) {
        log.info("Usuario {} publicando Pokémon {} en el mercado por {} monedas",
                userId, request.pokemonName(), request.price());

        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar que el Pokémon está en la colección del vendedor
        CapturedPokemon captured = capturedPokemonRepository
                .findByUserIdAndPokemonId(userId, request.pokemonId())
                .orElseThrow(() -> new BadRequestException(
                        "No tienes a " + request.pokemonName() + " en tu colección"));

        // Verificar que no esté ya publicado
        if (marketListingRepository.existsBySellerIdAndPokemonIdAndStatus(
                userId, request.pokemonId(), ListingStatus.ACTIVE)) {
            throw new ConflictException("Ya tienes a " + request.pokemonName() + " publicado en el mercado");
        }

        // Crear la publicación
        MarketListing listing = MarketListing.builder()
                .seller(seller)
                .pokemonId(request.pokemonId())
                .pokemonName(request.pokemonName())
                .price(request.price())
                .build();

        listing = marketListingRepository.save(listing);

        // Remover de la colección del vendedor (ya está en venta)
        capturedPokemonRepository.delete(captured);

        log.info("Publicación {} creada exitosamente", listing.getId());
        return toResponse(listing);
    }

    /**
     * Explorar el mercado: listar publicaciones activas con paginación y búsqueda opcional.
     */
    public Page<MarketListingResponse> explore(String search, Pageable pageable) {
        Page<MarketListing> listings;

        if (search != null && !search.isBlank()) {
            listings = marketListingRepository.findByStatusAndPokemonNameContainingIgnoreCase(
                    ListingStatus.ACTIVE, search.trim(), pageable);
        } else {
            listings = marketListingRepository.findByStatus(ListingStatus.ACTIVE, pageable);
        }

        return listings.map(this::toResponse);
    }

    /**
     * Comprar un Pokémon del mercado.
     * Transfiere monedas del comprador al vendedor y el Pokémon al comprador.
     */
    @Transactional
    public PurchaseResponse buy(Long buyerId, Long listingId) {
        log.info("Usuario {} comprando publicación {}", buyerId, listingId);

        MarketListing listing = marketListingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada"));

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new BadRequestException("Esta publicación ya no está disponible");
        }

        if (listing.getSeller().getId().equals(buyerId)) {
            throw new BadRequestException("No puedes comprar tu propia publicación");
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar que el comprador tiene suficientes monedas
        if (buyer.getCoins() < listing.getPrice()) {
            throw new BadRequestException("No tienes suficientes monedas. Necesitas "
                    + listing.getPrice() + " pero tienes " + buyer.getCoins());
        }

        User seller = listing.getSeller();

        // Transferir monedas
        buyer.setCoins(buyer.getCoins() - listing.getPrice());
        seller.setCoins(seller.getCoins() + listing.getPrice());

        // Agregar Pokémon a la colección del comprador
        CapturedPokemon newCapture = CapturedPokemon.builder()
                .user(buyer)
                .pokemonId(listing.getPokemonId())
                .pokemonName(listing.getPokemonName())
                .build();
        capturedPokemonRepository.save(newCapture);

        // Actualizar la publicación
        listing.setStatus(ListingStatus.SOLD);
        listing.setBuyer(buyer);
        listing.setSoldAt(LocalDateTime.now());

        userRepository.save(buyer);
        userRepository.save(seller);
        marketListingRepository.save(listing);

        log.info("Compra exitosa: {} compró {} por {} monedas",
                buyer.getUsername(), listing.getPokemonName(), listing.getPrice());

        return new PurchaseResponse(
                listing.getId(),
                listing.getPokemonId(),
                listing.getPokemonName(),
                listing.getPrice(),
                buyer.getCoins()
        );
    }

    /**
     * Cancelar una publicación propia. Devuelve el Pokémon a la colección.
     */
    @Transactional
    public void cancel(Long userId, Long listingId) {
        log.info("Usuario {} cancelando publicación {}", userId, listingId);

        MarketListing listing = marketListingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación no encontrada"));

        if (!listing.getSeller().getId().equals(userId)) {
            throw new ForbiddenException("No puedes cancelar una publicación que no es tuya");
        }

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new BadRequestException("Esta publicación ya no está activa");
        }

        // Devolver Pokémon a la colección del vendedor
        CapturedPokemon returned = CapturedPokemon.builder()
                .user(listing.getSeller())
                .pokemonId(listing.getPokemonId())
                .pokemonName(listing.getPokemonName())
                .build();
        capturedPokemonRepository.save(returned);

        listing.setStatus(ListingStatus.CANCELLED);
        marketListingRepository.save(listing);

        log.info("Publicación {} cancelada", listingId);
    }

    /**
     * Ver mis publicaciones activas.
     */
    public List<MarketListingResponse> myListings(Long userId) {
        return marketListingRepository.findBySellerIdAndStatus(userId, ListingStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Ver mi historial de compras.
     */
    public List<MarketListingResponse> myPurchases(Long userId) {
        return marketListingRepository.findByBuyerId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MarketListingResponse toResponse(MarketListing listing) {
        return new MarketListingResponse(
                listing.getId(),
                listing.getSeller().getId(),
                listing.getSeller().getUsername(),
                listing.getPokemonId(),
                listing.getPokemonName(),
                listing.getPrice(),
                listing.getStatus().name(),
                listing.getCreatedAt()
        );
    }
}
