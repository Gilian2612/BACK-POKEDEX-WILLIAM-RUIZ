package com.wilddex.controller;

import com.wilddex.dto.admin.AdminStatsResponse;
import com.wilddex.dto.admin.AdminUserResponse;
import com.wilddex.dto.admin.UpdateRoleRequest;
import com.wilddex.dto.response.ApiResponse;
import com.wilddex.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administración", description = "Gestión de usuarios y estadísticas del sistema")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "Listar usuarios", description = "Lista todos los usuarios registrados con paginación")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> listUsers(Pageable pageable) {
        Page<AdminUserResponse> users = adminService.listUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Usuarios del sistema", users));
    }

    @PatchMapping("/users/{userId}/toggle")
    @Operation(summary = "Habilitar/deshabilitar usuario", description = "Alterna el estado enabled de un usuario")
    public ResponseEntity<ApiResponse<AdminUserResponse>> toggleUser(@PathVariable Long userId) {
        AdminUserResponse user = adminService.toggleUserEnabled(userId);
        return ResponseEntity.ok(ApiResponse.success("Estado del usuario actualizado", user));
    }

    @PatchMapping("/users/{userId}/role")
    @Operation(summary = "Cambiar rol", description = "Cambia el rol de un usuario entre USER y ADMIN")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRoleRequest request) {
        AdminUserResponse user = adminService.updateRole(userId, request.role());
        return ResponseEntity.ok(ApiResponse.success("Rol actualizado", user));
    }

    @GetMapping("/stats")
    @Operation(summary = "Estadísticas globales", description = "Total de usuarios, capturas, equipos y Pokémon más populares")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        AdminStatsResponse stats = adminService.getStats();
        return ResponseEntity.ok(ApiResponse.success("Estadísticas del sistema", stats));
    }
}