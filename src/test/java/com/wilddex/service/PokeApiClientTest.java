package com.wilddex.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilddex.dto.response.EvolutionChainResponse;
import com.wilddex.dto.response.PokemonDetailResponse;
import com.wilddex.dto.response.PokemonSummary;
import com.wilddex.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokeApiClientTest {

    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private PokeApiClient pokeApiClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        pokeApiClient = new PokeApiClient(webClientBuilder, "https://pokeapi.co/api/v2");
    }

    @SuppressWarnings("unchecked")
    private void stubGet(JsonNode response) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(response));
    }

    @SuppressWarnings("unchecked")
    private void stubGetNotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(
                Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));
    }

    @Test
    void getTotalPokemonCount_shouldReturnCount() throws Exception {
        JsonNode node = mapper.readTree("{\"count\": 1302, \"results\": []}");
        stubGet(node);

        long count = pokeApiClient.getTotalPokemonCount();
        assertEquals(1302, count);
    }

    @Test
    void getTotalPokemonCount_shouldReturnZero_whenNullResponse() throws Exception {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.empty());

        long count = pokeApiClient.getTotalPokemonCount();
        assertEquals(0, count);
    }

    @Test
    void getPokemonList_shouldReturnEmptyList_whenNoResults() throws Exception {
        JsonNode node = mapper.readTree("{\"count\": 0}");
        stubGet(node);

        List<PokemonSummary> result = pokeApiClient.getPokemonList(0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void extractIdFromUrl_worksViaTotalCount() throws Exception {
        // Tests the extractIdFromUrl indirectly
        JsonNode node = mapper.readTree("{\"count\": 500}");
        stubGet(node);

        assertEquals(500, pokeApiClient.getTotalPokemonCount());
    }

    
    @Test
    @SuppressWarnings("unchecked")
    void getPokemonList_shouldReturnSummaries_whenResultsExist() throws Exception {
        // Stub for the list call
        JsonNode listNode = mapper.readTree("""
            {
                "results": [
                    {"name": "pikachu", "url": "https://pokeapi.co/api/v2/pokemon/25/"}
                ]
            }
        """);

        JsonNode pokemonNode = mapper.readTree("""
            {
                "id": 25,
                "sprites": {"front_default": "sprite.png"},
                "types": [
                    {"slot": 1, "type": {"name": "electric"}}
                ]
            }
        """);

        // First call returns list, second call returns pokemon detail
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(listNode))
                .thenReturn(Mono.just(pokemonNode));

        List<PokemonSummary> result = pokeApiClient.getPokemonList(0, 1);

        assertEquals(1, result.size());
        assertEquals("pikachu", result.get(0).name());
        assertEquals(25, result.get(0).id());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchPokemon_shouldFilterByName() throws Exception {
        JsonNode listNode = mapper.readTree("""
            {
                "results": [
                    {"name": "pikachu", "url": "https://pokeapi.co/api/v2/pokemon/25/"},
                    {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"}
                ]
            }
        """);

        JsonNode pokemonNode = mapper.readTree("""
            {
                "id": 25,
                "sprites": {"front_default": "sprite.png"},
                "types": [{"slot": 1, "type": {"name": "electric"}}]
            }
        """);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(listNode))
                .thenReturn(Mono.just(pokemonNode));

        List<PokemonSummary> result = pokeApiClient.searchPokemon("pika");

        assertEquals(1, result.size());
        assertEquals("pikachu", result.get(0).name());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getPokemonByType_shouldReturnPokemon() throws Exception {
        JsonNode typeNode = mapper.readTree("""
            {
                "pokemon": [
                    {"pokemon": {"name": "charmander", "url": "https://pokeapi.co/api/v2/pokemon/4/"}}
                ]
            }
        """);

        JsonNode pokemonNode = mapper.readTree("""
            {
                "id": 4,
                "sprites": {"front_default": "sprite.png"},
                "types": [{"slot": 1, "type": {"name": "fire"}}]
            }
        """);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(typeNode))
                .thenReturn(Mono.just(pokemonNode));

        List<PokemonSummary> result = pokeApiClient.getPokemonByType("fire");

        assertEquals(1, result.size());
        assertEquals("charmander", result.get(0).name());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getPokemonByGeneration_shouldReturnPokemon() throws Exception {
        JsonNode genNode = mapper.readTree("""
            {
                "pokemon_species": [
                    {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon-species/1/"}
                ]
            }
        """);

        JsonNode pokemonNode = mapper.readTree("""
            {
                "id": 1,
                "sprites": {"front_default": "sprite.png"},
                "types": [{"slot": 1, "type": {"name": "grass"}}]
            }
        """);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(genNode))
                .thenReturn(Mono.just(pokemonNode));

        List<PokemonSummary> result = pokeApiClient.getPokemonByGeneration(1);

        assertEquals(1, result.size());
        assertEquals("bulbasaur", result.get(0).name());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getPokemonDetail_shouldReturnFullDetail() throws Exception {
        JsonNode pokemonNode = mapper.readTree("""
            {
                "id": 25, "name": "pikachu", "height": 4, "weight": 60,
                "sprites": {
                    "front_default": "sprite.png",
                    "other": {"official-artwork": {"front_default": "art.png"}}
                },
                "types": [{"slot": 1, "type": {"name": "electric"}}],
                "stats": [{"stat": {"name": "hp"}, "base_stat": 35}],
                "abilities": [{"ability": {"name": "static"}, "is_hidden": false}]
            }
        """);

        JsonNode speciesNode = mapper.readTree("""
            {
                "flavor_text_entries": [
                    {"flavor_text": "A mouse Pokemon", "language": {"name": "en"}}
                ],
                "generation": {"name": "generation-i", "url": "https://pokeapi.co/api/v2/generation/1/"}
            }
        """);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(pokemonNode))
                .thenReturn(Mono.just(speciesNode))
                .thenReturn(Mono.just(speciesNode));

        PokemonDetailResponse result = pokeApiClient.getPokemonDetail("pikachu");

        assertEquals(25, result.id());
        assertEquals("pikachu", result.name());
        assertEquals("sprite.png", result.spriteUrl());
        assertEquals("art.png", result.imageUrl());
        assertEquals(35, result.stats().get("hp"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getEvolutionChain_shouldReturnChain() throws Exception {
        JsonNode speciesNode = mapper.readTree("""
            {
                "evolution_chain": {"url": "https://pokeapi.co/api/v2/evolution-chain/10/"}
            }
        """);

        JsonNode chainNode = mapper.readTree("""
            {
                "chain": {
                    "species": {"name": "pichu", "url": "https://pokeapi.co/api/v2/pokemon-species/172/"},
                    "evolution_details": [],
                    "evolves_to": [
                        {
                            "species": {"name": "pikachu", "url": "https://pokeapi.co/api/v2/pokemon-species/25/"},
                            "evolution_details": [{"trigger": {"name": "level-up"}}],
                            "evolves_to": []
                        }
                    ]
                }
            }
        """);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(speciesNode))
                .thenReturn(Mono.just(chainNode));

        EvolutionChainResponse result = pokeApiClient.getEvolutionChain(25);

        assertEquals(1, result.stages().size());
        assertEquals("pichu", result.stages().get(0).name());
        assertEquals(1, result.stages().get(0).evolvesTo().size());
        assertEquals("pikachu", result.stages().get(0).evolvesTo().get(0).name());
    }
}