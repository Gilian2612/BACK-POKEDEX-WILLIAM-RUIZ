package com.wilddex.dto.market;

public record PurchaseResponse(
        Long listingId,
        Integer pokemonId,
        String pokemonName,
        Integer pricePaid,
        Integer remainingCoins
) {}
