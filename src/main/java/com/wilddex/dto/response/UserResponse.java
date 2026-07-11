package com.wilddex.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de salida con datos del usuario (no expone password ni datos sensibles).
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        String profileImageUrl,
        String role,
        String provider,
        boolean emailVerified,
        LocalDateTime createdAt
) {
}
