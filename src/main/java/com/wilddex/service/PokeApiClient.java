package com.wilddex.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wilddex.dto.response.EvolutionChainResponse;
import com.wilddex.dto.response.PokemonDetailResponse;
import com.wilddex.dto.response.PokemonSummary;
import com.wilddex.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Cliente para PokéAPI v2. Usa WebClient (no-blocking) con Caffeine cache.
 * PKX-005 a PKX-009.
 */
@Service
public class PokeApiClient {

    private static final Logger logger = LoggerFactory.getLogger(PokeApiClient.class);

    private final WebClient webClient;

    public PokeApiClient(WebClient.Builder webClientBuilder,
                         @Value("${app.pokeapi.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * PKX-005: Listado paginado de Pokémon (nombre, sprite, tipos).
     */
    @Cacheable(value = "pokemonList", key = "#offset + '-' + #limit")
    public List<PokemonSummary> getPokemonList(int offset, int limit) {
        logger.debug("Consultando PokéAPI: lista offset={}, limit={}", offset, limit);

        JsonNode listNode = webClient.get()
                .uri("/pokemon?offset={offset}&limit={limit}", offset, limit)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (listNode == null || !listNode.has("results")) {
            return List.of();
        }

        return StreamSupport.stream(listNode.get("results").spliterator(), false)
                .map(result -> {
                    String name = result.get("name").asText();
                    String url = result.get("url").asText();
                    int id = extractIdFromUrl(url);
                    return fetchPokemonSummary(id, name);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Obtener total de Pokémon disponibles en PokéAPI.
     */
    @Cacheable(value = "pokemonCount")
    public long getTotalPokemonCount() {
        JsonNode node = webClient.get()
                .uri("/pokemon?limit=1")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        return node != null && node.has("count") ? node.get("count").asLong() : 0;
    }

    /**
     * PKX-006/007: Buscar Pokémon por nombre (búsqueda parcial contra la lista completa).
     */
    @Cacheable(value = "pokemonSearch", key = "#query")
    public List<PokemonSummary> searchPokemon(String query) {
        logger.debug("Buscando Pokémon con query: {}", query);
        String lowerQuery = query.toLowerCase();

        // Obtener lista completa de nombres
        JsonNode listNode = webClient.get()
                .uri("/pokemon?limit=1302")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (listNode == null || !listNode.has("results")) {
            return List.of();
        }

        return StreamSupport.stream(listNode.get("results").spliterator(), false)
                .filter(r -> r.get("name").asText().contains(lowerQuery))
                .limit(20)
                .map(r -> {
                    String name = r.get("name").asText();
                    int id = extractIdFromUrl(r.get("url").asText());
                    return fetchPokemonSummary(id, name);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * PKX-007: Filtrar Pokémon por tipo.
     */
    @Cacheable(value = "pokemonByType", key = "#type")
    public List<PokemonSummary> getPokemonByType(String type) {
        logger.debug("Consultando PokéAPI: tipo={}", type);

        try {
            JsonNode typeNode = webClient.get()
                    .uri("/type/{type}", type.toLowerCase())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (typeNode == null || !typeNode.has("pokemon")) {
                return List.of();
            }

            return StreamSupport.stream(typeNode.get("pokemon").spliterator(), false)
                    .limit(50)
                    .map(entry -> {
                        JsonNode pokemonRef = entry.get("pokemon");
                        String name = pokemonRef.get("name").asText();
                        int id = extractIdFromUrl(pokemonRef.get("url").asText());
                        return fetchPokemonSummary(id, name);
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("Tipo de Pokémon no encontrado: " + type);
        }
    }

    /**
     * PKX-007: Filtrar Pokémon por generación.
     */
    @Cacheable(value = "pokemonByGeneration", key = "#generation")
    public List<PokemonSummary> getPokemonByGeneration(int generation) {
        logger.debug("Consultando PokéAPI: generación={}", generation);

        try {
            JsonNode genNode = webClient.get()
                    .uri("/generation/{id}", generation)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (genNode == null || !genNode.has("pokemon_species")) {
                return List.of();
            }

            return StreamSupport.stream(genNode.get("pokemon_species").spliterator(), false)
                    .map(species -> {
                        String name = species.get("name").asText();
                        int id = extractIdFromUrl(species.get("url").asText());
                        return fetchPokemonSummary(id, name);
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(PokemonSummary::id))
                    .toList();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("Generación no encontrada: " + generation);
        }
    }

    /**
     * PKX-008: Detalle completo de un Pokémon.
     */
    @Cacheable(value = "pokemonDetail", key = "#idOrName")
    public PokemonDetailResponse getPokemonDetail(String idOrName) {
        logger.debug("Consultando PokéAPI: detalle de {}", idOrName);

        try {
            JsonNode pokemon = webClient.get()
                    .uri("/pokemon/{idOrName}", idOrName.toLowerCase())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (pokemon == null) {
                throw new ResourceNotFoundException("Pokémon no encontrado: " + idOrName);
            }

            int id = pokemon.get("id").asInt();
            String name = pokemon.get("name").asText();

            // Sprite e imagen oficial
            JsonNode sprites = pokemon.get("sprites");
            String spriteUrl = sprites.get("front_default").asText(null);
            String imageUrl = null;
            if (sprites.has("other") && sprites.get("other").has("official-artwork")) {
                imageUrl = sprites.get("other").get("official-artwork").get("front_default").asText(null);
            }

            // Tipos
            List<String> types = StreamSupport.stream(pokemon.get("types").spliterator(), false)
                    .sorted(Comparator.comparingInt(t -> t.get("slot").asInt()))
                    .map(t -> t.get("type").get("name").asText())
                    .toList();

            // Stats
            Map<String, Integer> stats = new LinkedHashMap<>();
            StreamSupport.stream(pokemon.get("stats").spliterator(), false)
                    .forEach(s -> stats.put(s.get("stat").get("name").asText(), s.get("base_stat").asInt()));

            // Abilities
            List<PokemonDetailResponse.AbilityInfo> abilities = StreamSupport.stream(
                    pokemon.get("abilities").spliterator(), false)
                    .map(a -> new PokemonDetailResponse.AbilityInfo(
                            a.get("ability").get("name").asText(),
                            a.get("is_hidden").asBoolean()))
                    .toList();

            // Descripción desde species
            String description = fetchDescription(id);

            // Generación y región desde species
            int generation = 0;
            String region = null;
            try {
                JsonNode species = webClient.get()
                        .uri("/pokemon-species/{id}", id)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();
                if (species != null && species.has("generation")) {
                    String genUrl = species.get("generation").get("url").asText();
                    generation = extractIdFromUrl(genUrl);
                    region = species.get("generation").get("name").asText()
                            .replace("generation-", "");
                }
            } catch (Exception e) {
                logger.warn("No se pudo obtener species de {}: {}", id, e.getMessage());
            }

            return new PokemonDetailResponse(id, name, spriteUrl, imageUrl, types, stats,
                    abilities, description, pokemon.get("height").asInt(),
                    pokemon.get("weight").asInt(), generation, region);

        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("Pokémon no encontrado: " + idOrName);
        }
    }

    /**
     * PKX-009: Cadena evolutiva de un Pokémon.
     */
    @Cacheable(value = "evolutionChain", key = "#pokemonId")
    public EvolutionChainResponse getEvolutionChain(int pokemonId) {
        logger.debug("Consultando cadena evolutiva para Pokémon ID: {}", pokemonId);

        try {
            // 1. Obtener species → evolution_chain URL
            JsonNode species = webClient.get()
                    .uri("/pokemon-species/{id}", pokemonId)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (species == null || !species.has("evolution_chain")) {
                throw new ResourceNotFoundException("No se encontró cadena evolutiva para Pokémon ID: " + pokemonId);
            }

            String chainUrl = species.get("evolution_chain").get("url").asText();

            // 2. Fetch evolution chain
            JsonNode chain = webClient.get()
                    .uri(chainUrl)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (chain == null || !chain.has("chain")) {
                return new EvolutionChainResponse(List.of());
            }

            // 3. Parsear cadena recursiva
            EvolutionChainResponse.EvolutionStage root = parseEvolutionStage(chain.get("chain"));
            return new EvolutionChainResponse(List.of(root));

        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("Pokémon no encontrado: " + pokemonId);
        }
    }

    // ── Helpers privados ──

    private PokemonSummary fetchPokemonSummary(int id, String name) {
        try {
            JsonNode pokemon = webClient.get()
                    .uri("/pokemon/{id}", id)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (pokemon == null) return null;

            String spriteUrl = pokemon.get("sprites").get("front_default").asText(null);
            List<String> types = StreamSupport.stream(pokemon.get("types").spliterator(), false)
                    .sorted(Comparator.comparingInt(t -> t.get("slot").asInt()))
                    .map(t -> t.get("type").get("name").asText())
                    .toList();

            return new PokemonSummary(id, name, spriteUrl, types);
        } catch (Exception e) {
            logger.warn("No se pudo obtener resumen del Pokémon {}: {}", id, e.getMessage());
            return null;
        }
    }

    private String fetchDescription(int pokemonId) {
        try {
            JsonNode species = webClient.get()
                    .uri("/pokemon-species/{id}", pokemonId)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (species != null && species.has("flavor_text_entries")) {
                return StreamSupport.stream(species.get("flavor_text_entries").spliterator(), false)
                        .filter(entry -> "es".equals(entry.get("language").get("name").asText()))
                        .findFirst()
                        .or(() -> StreamSupport.stream(species.get("flavor_text_entries").spliterator(), false)
                                .filter(entry -> "en".equals(entry.get("language").get("name").asText()))
                                .findFirst())
                        .map(entry -> entry.get("flavor_text").asText().replaceAll("[\\n\\f\\r]", " "))
                        .orElse(null);
            }
        } catch (Exception e) {
            logger.warn("No se pudo obtener descripción del Pokémon {}: {}", pokemonId, e.getMessage());
        }
        return null;
    }

    private EvolutionChainResponse.EvolutionStage parseEvolutionStage(JsonNode chainNode) {
        String speciesName = chainNode.get("species").get("name").asText();
        int speciesId = extractIdFromUrl(chainNode.get("species").get("url").asText());
        String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + speciesId + ".png";

        // Trigger de evolución
        String trigger = null;
        if (chainNode.has("evolution_details") && chainNode.get("evolution_details").size() > 0) {
            JsonNode details = chainNode.get("evolution_details").get(0);
            if (details.has("trigger") && !details.get("trigger").isNull()) {
                trigger = details.get("trigger").get("name").asText();
            }
        }

        // Parsear evoluciones siguientes (recursivo)
        List<EvolutionChainResponse.EvolutionStage> evolvesTo = new ArrayList<>();
        if (chainNode.has("evolves_to")) {
            for (JsonNode next : chainNode.get("evolves_to")) {
                evolvesTo.add(parseEvolutionStage(next));
            }
        }

        return new EvolutionChainResponse.EvolutionStage(speciesId, speciesName, spriteUrl, trigger, evolvesTo);
    }

    private int extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
