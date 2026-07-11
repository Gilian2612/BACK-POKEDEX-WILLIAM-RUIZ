package com.wilddex.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Respuesta uniforme de la API (sección 10.5 DOSW).
 * Todos los endpoints retornan esta estructura.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>("SUCCESS", message, null);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
