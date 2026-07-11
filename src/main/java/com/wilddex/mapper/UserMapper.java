package com.wilddex.mapper;

import com.wilddex.dto.response.UserResponse;
import com.wilddex.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper entre entidad User y DTOs de respuesta.
 * Separa conversión entre capas (sección 9.5 DOSW).
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getRole().name(),
                user.getProvider().name(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}
