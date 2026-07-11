package com.wilddex.controller;

import com.wilddex.dto.request.LoginRequest;
import com.wilddex.dto.request.RegisterRequest;
import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.response.AuthResponse;
import com.wilddex.dto.response.UserResponse;
import com.wilddex.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthService authService;
    @InjectMocks private AuthController authController;

    @Test
    void register_shouldReturn201() {
        when(authService.register(any())).thenReturn(null);
        RegisterRequest req = new RegisterRequest("ash", "ash@pokemon.com", "Pass123!", "Pass123!");

        ResponseEntity<ApiResponse<UserResponse>> response = authController.register(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("CREATED", response.getBody().code());
    }

    @Test
    void login_shouldReturn200() {
        LoginRequest req = new LoginRequest("ash@pokemon.com", "Pass123!");

        ResponseEntity<ApiResponse<Void>> response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).login(req);
    }

    @Test
    void verifyOtp_shouldReturnTokens() {
        AuthResponse auth = new AuthResponse("access", "refresh", null);
        when(authService.verifyOtp("ash@pokemon.com", "123456")).thenReturn(auth);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.verifyOtp("ash@pokemon.com", "123456");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("access", response.getBody().data().accessToken());
    }

    @Test
    void resendOtp_shouldReturn200() {
        ResponseEntity<ApiResponse<Void>> response = authController.resendOtp("ash@pokemon.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).resendOtp("ash@pokemon.com");
    }

    @Test
    void oauth2Google_shouldReturnTokens() {
        AuthResponse auth = new AuthResponse("token", "refresh", null);
        when(authService.processOAuth2User(any(), any(), any(), any())).thenReturn(auth);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.oauth2Google(
                "ash@gmail.com", "Ash", "pic.jpg", "google123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody().data().accessToken());
    }
}