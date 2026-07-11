package com.wilddex.mapper;

import com.wilddex.dto.response.CollectionItemResponse;
import com.wilddex.model.CapturedPokemon;
import com.wilddex.model.FavoritePokemon;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public CollectionItemResponse toResponse(CapturedPokemon captured) {
        return new CollectionItemResponse(
                captured.getId(),
                captured.getPokemonId(),
                captured.getPokemonName(),
                captured.getCapturedAt()
        );
    }

    public CollectionItemResponse toResponse(FavoritePokemon favorite) {
        return new CollectionItemResponse(
                favorite.getId(),
                favorite.getPokemonId(),
                favorite.getPokemonName(),
                favorite.getFavoritedAt()
        );
    }
}
