package com.wilddex.dto.admin;

public record AdminStatsResponse(
        long totalUsers,
        long totalCaptures,
        long totalTeams,
        long totalMarketListings,
        long activeListings,
        String mostCapturedPokemon,
        String mostFavoritedPokemon
) {}