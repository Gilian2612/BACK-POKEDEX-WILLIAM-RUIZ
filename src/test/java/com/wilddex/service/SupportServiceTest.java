package com.wilddex.service;

import com.wilddex.dto.support.ChatRequest;
import com.wilddex.dto.support.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportServiceTest {

    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private SupportService supportService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader(any(), any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        supportService = new SupportService(webClientBuilder, "test-api-key", "claude-haiku-4-5-20251001");
    }

    @SuppressWarnings("unchecked")
    private void stubPostChain() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenAnswer(i -> requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @SuppressWarnings("unchecked")
    void chat_shouldReturnReply_whenApiResponds() {
        ChatRequest request = new ChatRequest("Hola", List.of());
        stubPostChain();

        Map<String, Object> apiResponse = Map.of(
                "content", List.of(Map.of("text", "¡Hola! Soy el asistente de WildDex."))
        );
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(apiResponse));

        ChatResponse response = supportService.chat(request);

        assertNotNull(response);
        assertTrue(response.reply().contains("asistente"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void chat_shouldThrow_whenApiReturnsNull() {
        ChatRequest request = new ChatRequest("Hola", List.of());
        stubPostChain();
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.empty());

        assertThrows(RuntimeException.class, () -> supportService.chat(request));
    }
}