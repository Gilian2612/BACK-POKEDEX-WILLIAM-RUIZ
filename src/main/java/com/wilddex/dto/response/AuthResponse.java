package com.wilddex.dto.response;

/**
 * Respuesta de autenticación con tokens JWT.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UserResponse user
) {
    public AuthResponse(String accessToken, String refreshToken, UserResponse user) {
        this(accessToken, refreshToken, "Bearer", user);
    }
}
