package com.wilddex.controller;

import com.wilddex.dto.request.LoginRequest;
import com.wilddex.dto.request.RegisterRequest;
import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.response.AuthResponse;
import com.wilddex.dto.response.UserResponse;
import com.wilddex.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticación (PKX-001, PKX-002, PKX-003).
 * Endpoints públicos bajo /api/v1/auth/**.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro, login con OTP, OAuth2 Google")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "PKX-001: Crea cuenta local y envía OTP por email")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("CREATED", "Usuario registrado. Revisa tu email para el código de verificación.", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "PKX-002: Valida credenciales y envía OTP por email")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody LoginRequest request) {
        authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Código OTP enviado a tu correo electrónico"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verificar OTP", description = "PKX-002: Verifica código OTP y retorna tokens JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@RequestParam String email,
                                                                @RequestParam String code) {
        AuthResponse auth = authService.verifyOtp(email, code);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", auth));
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Reenviar OTP", description = "Reenvía código de verificación al email")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestParam String email) {
        authService.resendOtp(email);
        return ResponseEntity.ok(ApiResponse.ok("Código OTP reenviado"));
    }

    @PostMapping("/oauth2/google")
    @Operation(summary = "Login con Google", description = "PKX-001/002: Procesa autenticación OAuth2 Google")
    public ResponseEntity<ApiResponse<AuthResponse>> oauth2Google(@RequestParam String email,
                                                                   @RequestParam String name,
                                                                   @RequestParam(required = false) String picture,
                                                                   @RequestParam String providerId) {
        AuthResponse auth = authService.processOAuth2User(email, name, picture, providerId);
        return ResponseEntity.ok(ApiResponse.success("Login con Google exitoso", auth));
    }
}
