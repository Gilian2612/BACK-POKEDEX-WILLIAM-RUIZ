package com.wilddex.service;

import com.wilddex.dto.request.TeamMemberRequest;
import com.wilddex.dto.request.TeamRequest;
import com.wilddex.dto.response.TeamResponse;
import com.wilddex.exception.BadRequestException;
import com.wilddex.exception.ForbiddenException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.mapper.TeamMapper;
import com.wilddex.model.Team;
import com.wilddex.model.User;
import com.wilddex.repository.TeamRepository;
import com.wilddex.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private UserRepository userRepository;
    @Mock private TeamMapper teamMapper;

    @InjectMocks private TeamService teamService;

    private User user;
    private Team team;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("ash").build();
        team = Team.builder().id(1L).name("Team Rocket").user(user).build();
    }

    @Test
    void createTeam_shouldSave_whenValidRequest() {
        TeamRequest request = new TeamRequest("Team Rocket", "Meowth, that's right!", List.of(
                new TeamMemberRequest(25, "pikachu", 1)
        ));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> {
            Team t = i.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(teamMapper.toResponse(any(Team.class))).thenReturn(null);

        assertDoesNotThrow(() -> teamService.createTeam(1L, request));
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void createTeam_shouldFail_whenMoreThan6Members() {
        List<TeamMemberRequest> members = List.of(
                new TeamMemberRequest(1, "bulbasaur", 1),
                new TeamMemberRequest(4, "charmander", 2),
                new TeamMemberRequest(7, "squirtle", 3),
                new TeamMemberRequest(25, "pikachu", 4),
                new TeamMemberRequest(39, "jigglypuff", 5),
                new TeamMemberRequest(52, "meowth", 6),
                new TeamMemberRequest(54, "psyduck", 7)
        );
        TeamRequest request = new TeamRequest("Too Big", null, members);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> teamService.createTeam(1L, request));
    }

    @Test
    void createTeam_shouldFail_whenDuplicateSlots() {
        List<TeamMemberRequest> members = List.of(
                new TeamMemberRequest(25, "pikachu", 1),
                new TeamMemberRequest(4, "charmander", 1)
        );
        TeamRequest request = new TeamRequest("Dupes", null, members);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> teamService.createTeam(1L, request));
    }

    @Test
    void getTeamById_shouldReturn_whenOwner() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamMapper.toResponse(team)).thenReturn(null);

        assertDoesNotThrow(() -> teamService.getTeamById(1L, 1L));
    }

    @Test
    void getTeamById_shouldFail_whenNotOwner() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        assertThrows(ForbiddenException.class, () -> teamService.getTeamById(1L, 99L));
    }

    @Test
    void deleteTeam_shouldDelete_whenOwner() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        assertDoesNotThrow(() -> teamService.deleteTeam(1L, 1L));
        verify(teamRepository).delete(team);
    }

    @Test
    void deleteTeam_shouldFail_whenNotFound() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> teamService.deleteTeam(99L, 1L));
    }

    @Test
    void getUserTeams_shouldReturnList() {
        when(teamRepository.findByUserId(1L)).thenReturn(List.of(team));
        when(teamMapper.toResponse(team)).thenReturn(null);

        List<TeamResponse> result = teamService.getUserTeams(1L);
        assertEquals(1, result.size());
    }
}