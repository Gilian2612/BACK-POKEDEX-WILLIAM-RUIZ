package com.wilddex.repository;

import com.wilddex.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamIdOrderBySlotAsc(Long teamId);

    /** Cuenta cuántos equipos incluyen a un Pokémon específico (tasa de elección). */
    @Query("SELECT COUNT(DISTINCT tm.team.id) FROM TeamMember tm WHERE tm.pokemonId = :pokemonId")
    long countTeamsWithPokemon(Integer pokemonId);
}
