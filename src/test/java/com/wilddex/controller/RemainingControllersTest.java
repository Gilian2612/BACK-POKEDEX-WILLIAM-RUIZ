/**
 * includes Team, admin, user and supoport controllers tests
 */


package com.wilddex.controller;

import com.wilddex.dto.admin.AdminStatsResponse;
import com.wilddex.dto.admin.AdminUserResponse;
import com.wilddex.dto.admin.UpdateRoleRequest;
import com.wilddex.dto.request.TeamRequest;
import com.wilddex.dto.request.UpdateProfileRequest;
import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.response.CompareResponse;
import com.wilddex.dto.response.TeamResponse;
import com.wilddex.dto.response.UserResponse;
import com.wilddex.dto.support.ChatRequest;
import com.wilddex.dto.support.ChatResponse;
import com.wilddex.model.AuthProvider;
import com.wilddex.model.Role;
import com.wilddex.model.User;
import com.wilddex.security.CustomUserDetails;
import com.wilddex.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemainingControllersTest {

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("ash").email("ash@pokemon.com")
                .password("enc").role(Role.USER).provider(AuthProvider.LOCAL).enabled(true).build();
        userDetails = new CustomUserDetails(user);
    }

    // ── TeamController ──

    @Test
    void team_list() {
        TeamService svc = mock(TeamService.class);
        TeamController ctrl = new TeamController(svc);
        when(svc.getUserTeams(1L)).thenReturn(List.of());
        ResponseEntity<ApiResponse<List<TeamResponse>>> resp = ctrl.list(userDetails);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void team_getById() {
        TeamService svc = mock(TeamService.class);
        TeamController ctrl = new TeamController(svc);
        when(svc.getTeamById(5L, 1L)).thenReturn(null);
        ResponseEntity<ApiResponse<TeamResponse>> resp = ctrl.getById(userDetails, 5L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void team_create() {
        TeamService svc = mock(TeamService.class);
        TeamController ctrl = new TeamController(svc);
        when(svc.createTeam(eq(1L), any())).thenReturn(null);
        TeamRequest req = new TeamRequest("Team Rocket", null, List.of());
        ResponseEntity<ApiResponse<TeamResponse>> resp = ctrl.create(userDetails, req);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    void team_update() {
        TeamService svc = mock(TeamService.class);
        TeamController ctrl = new TeamController(svc);
        when(svc.updateTeam(eq(5L), eq(1L), any())).thenReturn(null);
        TeamRequest req = new TeamRequest("Updated", null, List.of());
        ResponseEntity<ApiResponse<TeamResponse>> resp = ctrl.update(userDetails, 5L, req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void team_delete() {
        TeamService svc = mock(TeamService.class);
        TeamController ctrl = new TeamController(svc);
        ResponseEntity<ApiResponse<Void>> resp = ctrl.delete(userDetails, 5L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(svc).deleteTeam(5L, 1L);
    }

    // ── AdminController ──

    @Test
    void admin_listUsers() {
        AdminService svc = mock(AdminService.class);
        AdminController ctrl = new AdminController(svc);
        when(svc.listUsers(any())).thenReturn(new PageImpl<>(List.of()));
        ResponseEntity<ApiResponse<Page<AdminUserResponse>>> resp = ctrl.listUsers(Pageable.unpaged());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void admin_toggleUser() {
        AdminService svc = mock(AdminService.class);
        AdminController ctrl = new AdminController(svc);
        when(svc.toggleUserEnabled(1L)).thenReturn(null);
        ResponseEntity<ApiResponse<AdminUserResponse>> resp = ctrl.toggleUser(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void admin_updateRole() {
        AdminService svc = mock(AdminService.class);
        AdminController ctrl = new AdminController(svc);
        when(svc.updateRole(1L, "ADMIN")).thenReturn(null);
        ResponseEntity<ApiResponse<AdminUserResponse>> resp = ctrl.updateRole(1L, new UpdateRoleRequest("ADMIN"));
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void admin_getStats() {
        AdminService svc = mock(AdminService.class);
        AdminController ctrl = new AdminController(svc);
        when(svc.getStats()).thenReturn(null);
        ResponseEntity<ApiResponse<AdminStatsResponse>> resp = ctrl.getStats();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    // ── UserController ──

    @Test
    void user_getProfile() {
        UserService svc = mock(UserService.class);
        UserController ctrl = new UserController(svc);
        when(svc.getProfile(1L)).thenReturn(null);
        ResponseEntity<ApiResponse<UserResponse>> resp = ctrl.getProfile(userDetails);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void user_updateProfile() {
        UserService svc = mock(UserService.class);
        UserController ctrl = new UserController(svc);
        when(svc.updateProfile(eq(1L), any())).thenReturn(null);
        UpdateProfileRequest req = new UpdateProfileRequest("misty", null, null, null);
        ResponseEntity<ApiResponse<UserResponse>> resp = ctrl.updateProfile(userDetails, req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    // ── SupportController ──

    @Test
    void support_chat() {
        SupportService svc = mock(SupportService.class);
        SupportController ctrl = new SupportController(svc);
        when(svc.chat(any())).thenReturn(new ChatResponse("Hola!"));
        ChatRequest req = new ChatRequest("Hola", List.of());
        ResponseEntity<ApiResponse<ChatResponse>> resp = ctrl.chat(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Hola!", resp.getBody().data().reply());
    }

    // ── CompareController ──

    @Test
    void compare_shouldReturn200() {
        CompareService svc = mock(CompareService.class);
        CompareController ctrl = new CompareController(svc);
        when(svc.compare("pikachu", "charmander")).thenReturn(null);
        ResponseEntity<ApiResponse<CompareResponse>> resp = ctrl.compare("pikachu", "charmander");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}