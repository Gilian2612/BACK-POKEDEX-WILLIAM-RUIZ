package com.wilddex.dto.response;

import java.time.LocalDateTime;

/**
 * Respuesta de error uniforme (sección 10.6 DOSW).
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path
) {
}
