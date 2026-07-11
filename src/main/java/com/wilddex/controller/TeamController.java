package com.wilddex.controller;

import com.wilddex.dto.request.TeamRequest;
import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.response.TeamResponse;
import com.wilddex.security.CustomUserDetails;
import com.wilddex.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de equipos Pokémon (PKX-012).
 */
@RestController
@RequestMapping("/api/v1/teams")
@Tag(name = "Equipos", description = "CRUD de equipos Pokémon (máx. 6 miembros)")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    @Operation(summary = "Listar equipos", description = "PKX-012: Equipos del usuario autenticado")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> list(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TeamResponse> teams = teamService.getUserTeams(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Equipos del usuario", teams));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener equipo", description = "PKX-012: Detalle de un equipo")
    public ResponseEntity<ApiResponse<TeamResponse>> getById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        TeamResponse team = teamService.getTeamById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Detalle del equipo", team));
    }

    @PostMapping
    @Operation(summary = "Crear equipo", description = "PKX-012: Crear nuevo equipo Pokémon")
    public ResponseEntity<ApiResponse<TeamResponse>> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TeamRequest request) {
        TeamResponse team = teamService.createTeam(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("CREATED", "Equipo creado exitosamente", team));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar equipo", description = "PKX-012: Modificar equipo existente")
    public ResponseEntity<ApiResponse<TeamResponse>> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TeamRequest request) {
        TeamResponse team = teamService.updateTeam(id, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Equipo actualizado", team));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar equipo", description = "PKX-012: Eliminar un equipo")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        teamService.deleteTeam(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Equipo eliminado"));
    }
}
