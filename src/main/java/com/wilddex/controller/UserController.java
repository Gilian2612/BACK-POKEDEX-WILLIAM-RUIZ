package com.wilddex.controller;

import com.wilddex.dto.request.UpdateProfileRequest;
import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.response.UserResponse;
import com.wilddex.security.CustomUserDetails;
import com.wilddex.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de perfil de usuario (PKX-004).
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Gestión de perfil de usuario")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil actual", description = "PKX-004: Retorna datos del usuario autenticado")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse user = userService.getProfile(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Perfil obtenido", user));
    }

    @PatchMapping("/me")
    @Operation(summary = "Actualizar perfil", description = "PKX-004: Actualiza username, imagen o contraseña")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse user = userService.updateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Perfil actualizado", user));
    }
}
