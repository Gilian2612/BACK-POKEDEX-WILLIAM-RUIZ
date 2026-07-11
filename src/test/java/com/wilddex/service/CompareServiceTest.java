package com.wilddex.service;

import com.wilddex.dto.response.CompareResponse;
import com.wilddex.dto.response.PokemonDetailResponse;
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
class CompareServiceTest {

    @Mock private PokeApiClient pokeApiClient;
    @InjectMocks private CompareService compareService;

    private PokemonDetailResponse makeDetail(int id, String name, int hp, int atk) {
        return new PokemonDetailResponse(id, name, "sprite.png", "image.png",
                List.of("electric"), Map.of("hp", hp, "attack", atk),
                List.of(), "desc", 4, 60, 1, "kanto");
    }

    @Test
    void compare_shouldReturnBothPokemonWithStats() {
        when(pokeApiClient.getPokemonDetail("pikachu")).thenReturn(makeDetail(25, "pikachu", 35, 55));
        when(pokeApiClient.getPokemonDetail("charmander")).thenReturn(makeDetail(4, "charmander", 39, 52));

        CompareResponse result = compareService.compare("pikachu", "charmander");

        assertNotNull(result);
        assertEquals("pikachu", result.pokemon1().name());
        assertEquals("charmander", result.pokemon2().name());
        assertEquals(90, result.pokemon1().totalStats());
        assertEquals(91, result.pokemon2().totalStats());
    }

    @Test
    void compare_shouldDelegateToPokeApiClient() {
        when(pokeApiClient.getPokemonDetail("bulbasaur")).thenReturn(makeDetail(1, "bulbasaur", 45, 49));
        when(pokeApiClient.getPokemonDetail("squirtle")).thenReturn(makeDetail(7, "squirtle", 44, 48));

        compareService.compare("bulbasaur", "squirtle");

        verify(pokeApiClient).getPokemonDetail("bulbasaur");
        verify(pokeApiClient).getPokemonDetail("squirtle");
    }
}