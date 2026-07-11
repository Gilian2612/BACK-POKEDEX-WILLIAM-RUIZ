package com.wilddex.service;

import com.wilddex.exception.ConflictException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.mapper.CollectionMapper;
import com.wilddex.model.CapturedPokemon;
import com.wilddex.model.FavoritePokemon;
import com.wilddex.model.User;
import com.wilddex.repository.CapturedPokemonRepository;
import com.wilddex.repository.FavoritePokemonRepository;
import com.wilddex.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock private CapturedPokemonRepository capturedRepo;
    @Mock private FavoritePokemonRepository favoriteRepo;
    @Mock private UserRepository userRepository;
    @Mock private CollectionMapper collectionMapper;

    @InjectMocks private CollectionService collectionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("ash").build();
    }

    // ── Capture ──

    @Test
    void capturePokemon_shouldSucceed() {
        when(capturedRepo.existsByUserIdAndPokemonId(1L, 25)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(capturedRepo.save(any(CapturedPokemon.class))).thenAnswer(i -> i.getArgument(0));
        when(collectionMapper.toResponse(any(CapturedPokemon.class))).thenReturn(null);

        assertDoesNotThrow(() -> collectionService.capturePokemon(1L, 25, "pikachu"));
        verify(capturedRepo).save(any(CapturedPokemon.class));
    }

    @Test
    void capturePokemon_shouldFail_whenAlreadyCaptured() {
        when(capturedRepo.existsByUserIdAndPokemonId(1L, 25)).thenReturn(true);
        assertThrows(ConflictException.class, () -> collectionService.capturePokemon(1L, 25, "pikachu"));
    }

    @Test
    void capturePokemon_shouldFail_whenUserNotFound() {
        when(capturedRepo.existsByUserIdAndPokemonId(1L, 25)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> collectionService.capturePokemon(1L, 25, "pikachu"));
    }

    // ── Release ──

    @Test
    void releasePokemon_shouldSucceed() {
        CapturedPokemon captured = CapturedPokemon.builder().user(user).pokemonId(25).build();
        when(capturedRepo.findByUserIdAndPokemonId(1L, 25)).thenReturn(Optional.of(captured));

        assertDoesNotThrow(() -> collectionService.releasePokemon(1L, 25));
        verify(capturedRepo).delete(captured);
    }

    @Test
    void releasePokemon_shouldFail_whenNotFound() {
        when(capturedRepo.findByUserIdAndPokemonId(1L, 25)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> collectionService.releasePokemon(1L, 25));
    }

    // ── Get Captured ──

    @Test
    void getCapturedPokemon_shouldReturnList() {
        CapturedPokemon c = CapturedPokemon.builder().user(user).pokemonId(25).pokemonName("pikachu").build();
        when(capturedRepo.findByUserId(1L)).thenReturn(List.of(c));
        when(collectionMapper.toResponse(any(CapturedPokemon.class))).thenReturn(null);

        assertEquals(1, collectionService.getCapturedPokemon(1L).size());
    }

    // ── Favorites ──

    @Test
    void addFavorite_shouldSucceed() {
        when(favoriteRepo.existsByUserIdAndPokemonId(1L, 25)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteRepo.save(any(FavoritePokemon.class))).thenAnswer(i -> i.getArgument(0));
        when(collectionMapper.toResponse(any(FavoritePokemon.class))).thenReturn(null);

        assertDoesNotThrow(() -> collectionService.addFavorite(1L, 25, "pikachu"));
        verify(favoriteRepo).save(any(FavoritePokemon.class));
    }

    @Test
    void addFavorite_shouldFail_whenAlreadyFavorite() {
        when(favoriteRepo.existsByUserIdAndPokemonId(1L, 25)).thenReturn(true);
        assertThrows(ConflictException.class, () -> collectionService.addFavorite(1L, 25, "pikachu"));
    }

    @Test
    void addFavorite_shouldFail_whenUserNotFound() {
        when(favoriteRepo.existsByUserIdAndPokemonId(1L, 25)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> collectionService.addFavorite(1L, 25, "pikachu"));
    }

    // ── Remove Favorite ──

    @Test
    void removeFavorite_shouldSucceed() {
        FavoritePokemon fav = FavoritePokemon.builder().user(user).pokemonId(25).build();
        when(favoriteRepo.findByUserIdAndPokemonId(1L, 25)).thenReturn(Optional.of(fav));

        assertDoesNotThrow(() -> collectionService.removeFavorite(1L, 25));
        verify(favoriteRepo).delete(fav);
    }

    @Test
    void removeFavorite_shouldFail_whenNotFound() {
        when(favoriteRepo.findByUserIdAndPokemonId(1L, 25)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> collectionService.removeFavorite(1L, 25));
    }

    // ── Get Favorites ──

    @Test
    void getFavoritePokemon_shouldReturnList() {
        FavoritePokemon f = FavoritePokemon.builder().user(user).pokemonId(25).pokemonName("pikachu").build();
        when(favoriteRepo.findByUserId(1L)).thenReturn(List.of(f));
        when(collectionMapper.toResponse(any(FavoritePokemon.class))).thenReturn(null);

        assertEquals(1, collectionService.getFavoritePokemon(1L).size());
    }
}