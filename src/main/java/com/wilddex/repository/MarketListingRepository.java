package com.wilddex.repository;

import com.wilddex.model.ListingStatus;
import com.wilddex.model.MarketListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketListingRepository extends JpaRepository<MarketListing, Long> {

    Page<MarketListing> findByStatus(ListingStatus status, Pageable pageable);

    Page<MarketListing> findByStatusAndPokemonNameContainingIgnoreCase(
            ListingStatus status, String pokemonName, Pageable pageable);

    List<MarketListing> findBySellerIdAndStatus(Long sellerId, ListingStatus status);

    List<MarketListing> findByBuyerId(Long buyerId);

    boolean existsBySellerIdAndPokemonIdAndStatus(Long sellerId, Integer pokemonId, ListingStatus status);
}
