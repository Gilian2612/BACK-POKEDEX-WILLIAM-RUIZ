package com.wilddex.service;


import com.wilddex.dto.request.LoginRequest;
import com.wilddex.dto.request.RegisterRequest;
import com.wilddex.exception.ConflictException;
import com.wilddex.model.User;
import com.wilddex.repository.UserRepository;
import com.wilddex.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private JavaMailSender mailSender;
    @Mock private com.wilddex.mapper.UserMapper userMapper;

    @InjectMocks private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("testuser", "test@email.com", "Password123!", "Password123!");
    }

    @Test
    void register_shouldCreateUser_whenEmailNotTaken() {
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        
        when(userMapper.toResponse(any(User.class))).thenReturn(null); 
        assertDoesNotThrow(() -> authService.register(registerRequest));
        verify(userRepository, atLeast(1)).save(any(User.class));
    }

    @Test
    void register_shouldThrowConflict_whenEmailExists() {
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldThrowConflict_whenUsernameExists() {
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }
}