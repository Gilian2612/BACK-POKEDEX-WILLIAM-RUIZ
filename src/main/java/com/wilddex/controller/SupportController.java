package com.wilddex.controller;

import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.support.ChatRequest;
import com.wilddex.dto.support.ChatResponse;
import com.wilddex.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support")
@Tag(name = "Soporte técnico", description = "Chat de soporte con IA")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Enviar mensaje al asistente de soporte",
            description = "Envía un mensaje y el historial de conversación para obtener una respuesta del asistente IA")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = supportService.chat(request);
        return ResponseEntity.ok(ApiResponse.success("Respuesta generada", response));
    }
}
