package com.wilddex.dto.market;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateListingRequest(
        @NotNull Integer pokemonId,
        @NotBlank String pokemonName,
        @NotNull @Min(1) Integer price
) {}
