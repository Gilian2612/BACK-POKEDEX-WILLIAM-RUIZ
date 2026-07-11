package com.wilddex.service;

import com.wilddex.exception.ConflictException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.model.CapturedPokemon;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock private CapturedPokemonRepository capturedPokemonRepository;
    @Mock private FavoritePokemonRepository favoritePokemonRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CollectionService collectionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("ash").build();
    }

    @Test
    void capturePokemon_shouldSave_whenNotAlreadyCaptured() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(capturedPokemonRepository.existsByUserIdAndPokemonId(1L, 25)).thenReturn(false);
        when(capturedPokemonRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> collectionService.capturePokemon(1L, 25, "pikachu"));
        verify(capturedPokemonRepository).save(any(CapturedPokemon.class));
    }

    @Test
    void capturePokemon_shouldThrow_whenAlreadyCaptured() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(capturedPokemonRepository.existsByUserIdAndPokemonId(1L, 25)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> collectionService.capturePokemon(1L, 25, "pikachu"));
    }

    @Test
    void releasePokemon_shouldDelete_whenExists() {
        CapturedPokemon captured = CapturedPokemon.builder()
                .id(1L).user(user).pokemonId(25).pokemonName("pikachu").build();
        when(capturedPokemonRepository.findByUserIdAndPokemonId(1L, 25))
                .thenReturn(Optional.of(captured));

        assertDoesNotThrow(() -> collectionService.releasePokemon(1L, 25));
        verify(capturedPokemonRepository).delete(captured);
    }

    @Test
    void releasePokemon_shouldThrow_whenNotFound() {
        when(capturedPokemonRepository.findByUserIdAndPokemonId(1L, 25))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> collectionService.releasePokemon(1L, 25));
    }
}