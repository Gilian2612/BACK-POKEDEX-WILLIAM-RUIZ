package com.wilddex.mapper;

import com.wilddex.dto.response.TeamResponse;
import com.wilddex.model.Team;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamMapper {

    public TeamResponse toResponse(Team team) {
        List<TeamResponse.TeamMemberResponse> members = team.getMembers().stream()
                .map(m -> new TeamResponse.TeamMemberResponse(
                        m.getId(),
                        m.getPokemonId(),
                        m.getPokemonName(),
                        m.getSlot()))
                .toList();

        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                members,
                team.getCreatedAt(),
                team.getUpdatedAt()
        );
    }
}
