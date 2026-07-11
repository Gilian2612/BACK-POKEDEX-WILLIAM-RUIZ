package com.wilddex.security;

import com.wilddex.model.AuthProvider;
import com.wilddex.model.Role;
import com.wilddex.model.User;
import com.wilddex.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = User.builder()
                .id(1L).username("ash").email("ash@pokemon.com")
                .password("encoded").role(Role.USER)
                .provider(AuthProvider.LOCAL).enabled(true)
                .build();
        when(userRepository.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("ash@pokemon.com");

        assertNotNull(result);
        assertEquals("ash@pokemon.com", result.getUsername());
        assertTrue(result.isEnabled());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_shouldThrow_whenNotFound() {
        when(userRepository.findByEmail("nobody@pokemon.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("nobody@pokemon.com"));
    }
}