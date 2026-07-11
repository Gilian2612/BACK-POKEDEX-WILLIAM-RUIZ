package com.wilddex.mapper;

import com.wilddex.dto.response.CollectionItemResponse;
import com.wilddex.dto.response.TeamResponse;
import com.wilddex.dto.response.UserResponse;
import com.wilddex.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    // ── UserMapper ──

    @Test
    void userMapper_shouldMapAllFields() {
        UserMapper mapper = new UserMapper();
        User user = User.builder()
                .id(1L).username("ash").email("ash@pokemon.com")
                .profileImageUrl("pic.jpg").role(Role.USER)
                .provider(AuthProvider.LOCAL).emailVerified(true)
                .build();
        user.setCreatedAt(LocalDateTime.now());

        UserResponse resp = mapper.toResponse(user);

        assertEquals(1L, resp.id());
        assertEquals("ash", resp.username());
        assertEquals("ash@pokemon.com", resp.email());
        assertEquals("pic.jpg", resp.profileImageUrl());
        assertEquals("USER", resp.role());
        assertEquals("LOCAL", resp.provider());
        assertTrue(resp.emailVerified());
    }

    // ── CollectionMapper ──

    @Test
    void collectionMapper_shouldMapCaptured() {
        CollectionMapper mapper = new CollectionMapper();
        CapturedPokemon captured = CapturedPokemon.builder()
                .id(1L).pokemonId(25).pokemonName("pikachu").build();
        captured.setCapturedAt(LocalDateTime.now());

        CollectionItemResponse resp = mapper.toResponse(captured);

        assertEquals(1L, resp.id());
        assertEquals(25, resp.pokemonId());
        assertEquals("pikachu", resp.pokemonName());
         assertNotNull(resp);
    }

    @Test
    void collectionMapper_shouldMapFavorite() {
        CollectionMapper mapper = new CollectionMapper();
        FavoritePokemon favorite = FavoritePokemon.builder()
                .id(2L).pokemonId(4).pokemonName("charmander").build();
        favorite.setFavoritedAt(LocalDateTime.now());

        CollectionItemResponse resp = mapper.toResponse(favorite);

        assertEquals(2L, resp.id());
        assertEquals(4, resp.pokemonId());
        assertEquals("charmander", resp.pokemonName());
    }

    // ── TeamMapper ──

    @Test
    void teamMapper_shouldMapTeamWithMembers() {
        TeamMapper mapper = new TeamMapper();
        User user = User.builder().id(1L).username("ash").build();

        TeamMember member = TeamMember.builder()
                .id(1L).pokemonId(25).pokemonName("pikachu").slot(1).build();

        Team team = Team.builder()
                .id(1L).name("Team Rocket").description("Meowth!")
                .user(user).members(List.of(member)).build();
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());

        TeamResponse resp = mapper.toResponse(team);

        assertEquals(1L, resp.id());
        assertEquals("Team Rocket", resp.name());
        assertEquals("Meowth!", resp.description());
        assertEquals(1, resp.members().size());
        assertEquals("pikachu", resp.members().get(0).pokemonName());
        assertEquals(1, resp.members().get(0).slot());
    }
}