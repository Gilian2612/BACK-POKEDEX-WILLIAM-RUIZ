package com.wilddex.service;

import com.wilddex.dto.response.CollectionItemResponse;
import com.wilddex.exception.ConflictException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.mapper.CollectionMapper;
import com.wilddex.model.CapturedPokemon;
import com.wilddex.model.FavoritePokemon;
import com.wilddex.model.User;
import com.wilddex.repository.CapturedPokemonRepository;
import com.wilddex.repository.FavoritePokemonRepository;
import com.wilddex.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de colección personal: capturas y favoritos (PKX-010, PKX-011, PKX-012).
 */
@Service
public class CollectionService {

    private static final Logger logger = LoggerFactory.getLogger(CollectionService.class);

    private final CapturedPokemonRepository capturedRepo;
    private final FavoritePokemonRepository favoriteRepo;
    private final UserRepository userRepository;
    private final CollectionMapper collectionMapper;

    public CollectionService(CapturedPokemonRepository capturedRepo,
                             FavoritePokemonRepository favoriteRepo,
                             UserRepository userRepository,
                             CollectionMapper collectionMapper) {
        this.capturedRepo = capturedRepo;
        this.favoriteRepo = favoriteRepo;
        this.userRepository = userRepository;
        this.collectionMapper = collectionMapper;
    }

    // ── PKX-010: Capturar Pokémon ──

    @Transactional
    public CollectionItemResponse capturePokemon(Long userId, int pokemonId, String pokemonName) {
        if (capturedRepo.existsByUserIdAndPokemonId(userId, pokemonId)) {
            throw new ConflictException("El Pokémon ya fue capturado");
        }
        User user = findUser(userId);

        CapturedPokemon captured = CapturedPokemon.builder()
                .user(user)
                .pokemonId(pokemonId)
                .pokemonName(pokemonName)
                .build();
        captured = capturedRepo.save(captured);
        logger.info("Pokémon {} capturado por usuario {}", pokemonName, userId);
        return collectionMapper.toResponse(captured);
    }

    @Transactional
    public void releasePokemon(Long userId, int pokemonId) {
        CapturedPokemon captured = capturedRepo.findByUserIdAndPokemonId(userId, pokemonId)
                .orElseThrow(() -> new ResourceNotFoundException("Pokémon no encontrado en capturas"));
        capturedRepo.delete(captured);
        logger.info("Pokémon {} liberado por usuario {}", pokemonId, userId);
    }

    public List<CollectionItemResponse> getCapturedPokemon(Long userId) {
        return capturedRepo.findByUserId(userId).stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    // ── PKX-011: Favoritos ──

    @Transactional
    public CollectionItemResponse addFavorite(Long userId, int pokemonId, String pokemonName) {
        if (favoriteRepo.existsByUserIdAndPokemonId(userId, pokemonId)) {
            throw new ConflictException("El Pokémon ya está en favoritos");
        }
        User user = findUser(userId);

        FavoritePokemon favorite = FavoritePokemon.builder()
                .user(user)
                .pokemonId(pokemonId)
                .pokemonName(pokemonName)
                .build();
        favorite = favoriteRepo.save(favorite);
        logger.info("Pokémon {} agregado a favoritos por usuario {}", pokemonName, userId);
        return collectionMapper.toResponse(favorite);
    }

    @Transactional
    public void removeFavorite(Long userId, int pokemonId) {
        FavoritePokemon favorite = favoriteRepo.findByUserIdAndPokemonId(userId, pokemonId)
                .orElseThrow(() -> new ResourceNotFoundException("Pokémon no encontrado en favoritos"));
        favoriteRepo.delete(favorite);
        logger.info("Pokémon {} removido de favoritos por usuario {}", pokemonId, userId);
    }

    public List<CollectionItemResponse> getFavoritePokemon(Long userId) {
        return favoriteRepo.findByUserId(userId).stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
