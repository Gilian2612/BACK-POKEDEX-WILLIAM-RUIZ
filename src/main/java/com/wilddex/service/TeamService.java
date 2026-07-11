package com.wilddex.service;

import com.wilddex.dto.request.TeamMemberRequest;
import com.wilddex.dto.request.TeamRequest;
import com.wilddex.dto.response.TeamResponse;
import com.wilddex.exception.BadRequestException;
import com.wilddex.exception.ForbiddenException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.mapper.TeamMapper;
import com.wilddex.model.Team;
import com.wilddex.model.TeamMember;
import com.wilddex.model.User;
import com.wilddex.repository.TeamRepository;
import com.wilddex.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio de equipos Pokémon (PKX-012).
 */
@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMapper teamMapper;

    public TeamService(TeamRepository teamRepository,
                       UserRepository userRepository,
                       TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamMapper = teamMapper;
    }

    public List<TeamResponse> getUserTeams(Long userId) {
        return teamRepository.findByUserId(userId).stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    public TeamResponse getTeamById(Long teamId, Long userId) {
        Team team = findTeamAndVerifyOwner(teamId, userId);
        return teamMapper.toResponse(team);
    }

    @Transactional
    public TeamResponse createTeam(Long userId, TeamRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Team team = Team.builder()
                .name(request.name())
                .description(request.description())
                .user(user)
                .build();

        if (request.members() != null && !request.members().isEmpty()) {
            validateMembers(request.members());
            for (TeamMemberRequest memberReq : request.members()) {
                TeamMember member = TeamMember.builder()
                        .team(team)
                        .pokemonId(memberReq.pokemonId())
                        .pokemonName(memberReq.pokemonName())
                        .slot(memberReq.slot())
                        .build();
                team.getMembers().add(member);
            }
        }

        team = teamRepository.save(team);
        logger.info("Equipo '{}' creado por usuario {}", request.name(), userId);
        return teamMapper.toResponse(team);
    }

    @Transactional
    public TeamResponse updateTeam(Long teamId, Long userId, TeamRequest request) {
        Team team = findTeamAndVerifyOwner(teamId, userId);

        team.setName(request.name());
        team.setDescription(request.description());

        // Reemplazar miembros
        team.getMembers().clear();
        if (request.members() != null && !request.members().isEmpty()) {
            validateMembers(request.members());
            for (TeamMemberRequest memberReq : request.members()) {
                TeamMember member = TeamMember.builder()
                        .team(team)
                        .pokemonId(memberReq.pokemonId())
                        .pokemonName(memberReq.pokemonName())
                        .slot(memberReq.slot())
                        .build();
                team.getMembers().add(member);
            }
        }

        team = teamRepository.save(team);
        logger.info("Equipo ID {} actualizado por usuario {}", teamId, userId);
        return teamMapper.toResponse(team);
    }

    @Transactional
    public void deleteTeam(Long teamId, Long userId) {
        Team team = findTeamAndVerifyOwner(teamId, userId);
        teamRepository.delete(team);
        logger.info("Equipo ID {} eliminado por usuario {}", teamId, userId);
    }

    // ── Helpers ──

    private Team findTeamAndVerifyOwner(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + teamId));
        if (!team.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No tiene permisos sobre este equipo");
        }
        return team;
    }

    private void validateMembers(List<TeamMemberRequest> members) {
        if (members.size() > 6) {
            throw new BadRequestException("Un equipo no puede tener más de 6 miembros");
        }
        Set<Integer> slots = new HashSet<>();
        for (TeamMemberRequest m : members) {
            if (!slots.add(m.slot())) {
                throw new BadRequestException("Slots duplicados en el equipo");
            }
        }
    }
}
