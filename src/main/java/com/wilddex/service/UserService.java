package com.wilddex.service;

import com.wilddex.dto.request.UpdateProfileRequest;
import com.wilddex.dto.response.UserResponse;
import com.wilddex.exception.BadRequestException;
import com.wilddex.exception.ConflictException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.mapper.UserMapper;
import com.wilddex.model.AuthProvider;
import com.wilddex.model.User;
import com.wilddex.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de gestión de perfil de usuario (PKX-004).
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /** Obtener perfil por ID. */
    public UserResponse getProfile(Long userId) {
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    /** Obtener perfil por email (usado con SecurityContext). */
    public UserResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return userMapper.toResponse(user);
    }

    /** PKX-004: Actualizar perfil (username, imagen, contraseña). */
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserById(userId);

        if (request.username() != null && !request.username().isBlank()) {
            if (!request.username().equals(user.getUsername()) && userRepository.existsByUsername(request.username())) {
                throw new ConflictException("El nombre de usuario ya está en uso");
            }
            user.setUsername(request.username());
        }

        if (request.profileImageUrl() != null) {
            user.setProfileImageUrl(request.profileImageUrl());
        }

        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            if (user.getProvider() == AuthProvider.GOOGLE && user.getPassword() == null) {
                throw new BadRequestException("Los usuarios de Google no pueden cambiar contraseña local");
            }
            if (request.currentPassword() == null || request.currentPassword().isBlank()) {
                throw new BadRequestException("Debe proporcionar la contraseña actual");
            }
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new BadRequestException("La contraseña actual es incorrecta");
            }
            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }

        user = userRepository.save(user);
        logger.info("Perfil actualizado para usuario ID: {}", userId);
        return userMapper.toResponse(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
    }
}
