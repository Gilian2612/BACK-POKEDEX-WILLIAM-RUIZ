package com.wilddex.dto.response;

import java.time.LocalDateTime;

/**
 * Elemento de la colección personal (capturado o favorito).
 */
public record CollectionItemResponse(
        Long id,
        int pokemonId,
        String pokemonName,
        LocalDateTime addedAt
) {
}
