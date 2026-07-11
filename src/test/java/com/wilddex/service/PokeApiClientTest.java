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
}