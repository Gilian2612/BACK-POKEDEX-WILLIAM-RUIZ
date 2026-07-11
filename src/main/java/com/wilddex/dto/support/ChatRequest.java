package com.wilddex.dto.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ChatRequest(
        @NotBlank String message,
        @NotNull List<ChatMessage> conversationHistory
) {}
