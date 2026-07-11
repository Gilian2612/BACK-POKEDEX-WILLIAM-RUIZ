package com.wilddex.dto.admin;
import java.time.LocalDateTime;

public record AdminUserResponse(
        Long id,
        String username,
        String email,
        String role,
        String provider,
        boolean enabled,
        boolean emailVerified,
        Integer coins,
        LocalDateTime createdAt
) {}