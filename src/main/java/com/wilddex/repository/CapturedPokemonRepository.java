package com.wilddex.repository;

import com.wilddex.model.CapturedPokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapturedPokemonRepository extends JpaRepository<CapturedPokemon, Long> {

    List<CapturedPokemon> findByUserId(Long userId);

    Optional<CapturedPokemon> findByUserIdAndPokemonId(Long userId, Integer pokemonId);

    boolean existsByUserIdAndPokemonId(Long userId, Integer pokemonId);

    long countByPokemonId(Integer pokemonId);
}
