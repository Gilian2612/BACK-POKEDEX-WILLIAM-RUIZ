package com.wilddex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración del WebClient para consumir PokéAPI.
 * Base URL configurable desde application.yml.
 */
@Configuration
public class WebClientConfig {

    @Value("${app.pokeapi.base-url}")
    private String pokeApiBaseUrl;

    @Bean
    public WebClient pokeApiWebClient() {
        return WebClient.builder()
                .baseUrl(pokeApiBaseUrl)
                .build();
    }
}
