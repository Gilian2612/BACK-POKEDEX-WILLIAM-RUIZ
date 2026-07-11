package com.wilddex.service;

import com.wilddex.dto.request.UpdateProfileRequest;
import com.wilddex.exception.BadRequestException;
import com.wilddex.exception.ConflictException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.mapper.UserMapper;
import com.wilddex.model.AuthProvider;
import com.wilddex.model.User;
import com.wilddex.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).username("ash").email("ash@pokemon.com")
                .password("encoded").provider(AuthProvider.LOCAL)
                .build();
    }

    @Test
    void getProfile_shouldReturn_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(null);

        assertDoesNotThrow(() -> userService.getProfile(1L));
        verify(userMapper).toResponse(user);
    }

    @Test
    void getProfile_shouldFail_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile(99L));
    }

    @Test
    void updateProfile_shouldUpdateUsername() {
        UpdateProfileRequest request = new UpdateProfileRequest("misty", null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("misty")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toResponse(any())).thenReturn(null);

        assertDoesNotThrow(() -> userService.updateProfile(1L, request));
        assertEquals("misty", user.getUsername());
    }

    @Test
    void updateProfile_shouldFail_whenUsernameConflict() {
        UpdateProfileRequest request = new UpdateProfileRequest("misty", null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("misty")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateProfile(1L, request));
    }

    @Test
    void updateProfile_shouldChangePassword() {
        UpdateProfileRequest request = new UpdateProfileRequest(null, null, "oldPass", "newPass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toResponse(any())).thenReturn(null);

        assertDoesNotThrow(() -> userService.updateProfile(1L, request));
        assertEquals("newEncoded", user.getPassword());
    }

    @Test
    void updateProfile_shouldFail_whenWrongCurrentPassword() {
        UpdateProfileRequest request = new UpdateProfileRequest(null, null, "wrongPass", "newPass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encoded")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.updateProfile(1L, request));
    }

    @Test
    void updateProfile_shouldFail_whenGoogleUserChangesPassword() {
        user.setProvider(AuthProvider.GOOGLE);
        user.setPassword(null);
        UpdateProfileRequest request = new UpdateProfileRequest(null, null, "old", "new");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.updateProfile(1L, request));
    }
}