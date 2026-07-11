package com.wilddex.dto.market;

import java.time.LocalDateTime;

public record MarketListingResponse(
        Long id,
        Long sellerId,
        String sellerUsername,
        Integer pokemonId,
        String pokemonName,
        Integer price,
        String status,
        LocalDateTime createdAt
) {}
