package com.wilddex.repository;

import com.wilddex.model.FavoritePokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePokemonRepository extends JpaRepository<FavoritePokemon, Long> {

    List<FavoritePokemon> findByUserId(Long userId);

    Optional<FavoritePokemon> findByUserIdAndPokemonId(Long userId, Integer pokemonId);

    boolean existsByUserIdAndPokemonId(Long userId, Integer pokemonId);

    long countByPokemonId(Integer pokemonId);
}
