package com.wilddex.service;

import com.wilddex.dto.admin.AdminUserResponse;
import com.wilddex.exception.BadRequestException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.model.AuthProvider;
import com.wilddex.model.Role;
import com.wilddex.model.User;
import com.wilddex.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CapturedPokemonRepository capturedPokemonRepository;
    @Mock private FavoritePokemonRepository favoritePokemonRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private MarketListingRepository marketListingRepository;

    @InjectMocks private AdminService adminService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).username("ash").email("ash@pokemon.com")
                .role(Role.USER).provider(AuthProvider.LOCAL)
                .enabled(true).emailVerified(true).coins(1000)
                .build();
    }

    @Test
    void toggleUserEnabled_shouldDisableUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AdminUserResponse response = adminService.toggleUserEnabled(1L);

        assertFalse(response.enabled());
    }

    @Test
    void toggleUserEnabled_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adminService.toggleUserEnabled(99L));
    }

    @Test
    void updateRole_shouldChangeToAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AdminUserResponse response = adminService.updateRole(1L, "ADMIN");

        assertEquals("ADMIN", response.role());
    }

    @Test
    void updateRole_shouldThrow_whenInvalidRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> adminService.updateRole(1L, "SUPERADMIN"));
    }
}