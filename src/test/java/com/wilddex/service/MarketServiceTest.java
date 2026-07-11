package com.wilddex.service;

import com.wilddex.dto.market.CreateListingRequest;
import com.wilddex.dto.market.MarketListingResponse;
import com.wilddex.dto.market.PurchaseResponse;
import com.wilddex.exception.BadRequestException;
import com.wilddex.model.*;
import com.wilddex.repository.CapturedPokemonRepository;
import com.wilddex.repository.MarketListingRepository;
import com.wilddex.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketServiceTest {

    @Mock private MarketListingRepository marketListingRepository;
    @Mock private CapturedPokemonRepository capturedPokemonRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private MarketService marketService;

    private User seller;
    private User buyer;
    private CapturedPokemon captured;

    @BeforeEach
    void setUp() {
        seller = User.builder().id(1L).username("seller").coins(1000).build();
        buyer = User.builder().id(2L).username("buyer").coins(1000).build();
        captured = CapturedPokemon.builder()
                .id(1L).user(seller).pokemonId(25).pokemonName("pikachu").build();
    }

    @Test
    void publish_shouldCreateListing_whenPokemonInCollection() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(capturedPokemonRepository.findByUserIdAndPokemonId(1L, 25))
                .thenReturn(Optional.of(captured));
        when(marketListingRepository.existsBySellerIdAndPokemonIdAndStatus(
                1L, 25, ListingStatus.ACTIVE)).thenReturn(false);
        when(marketListingRepository.save(any(MarketListing.class)))
                .thenAnswer(i -> {
                    MarketListing l = i.getArgument(0);
                    l.setId(1L);
                    return l;
                });

        CreateListingRequest request = new CreateListingRequest(25, "pikachu", 500);
        MarketListingResponse response = marketService.publish(1L, request);

        assertNotNull(response);
        assertEquals(500, response.price());
        verify(capturedPokemonRepository).delete(captured);
    }

    @Test
    void buy_shouldTransferCoinsAndPokemon() {
        MarketListing listing = MarketListing.builder()
                .id(1L).seller(seller).pokemonId(25).pokemonName("pikachu")
                .price(300).status(ListingStatus.ACTIVE).build();

        when(marketListingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(2L)).thenReturn(Optional.of(buyer));
        when(capturedPokemonRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(marketListingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseResponse response = marketService.buy(2L, 1L);

        assertEquals(700, response.remainingCoins());
        assertEquals(1300, seller.getCoins());
        assertEquals(ListingStatus.SOLD, listing.getStatus());
    }

    @Test
    void buy_shouldFail_whenInsufficientCoins() {
        buyer.setCoins(100);
        MarketListing listing = MarketListing.builder()
                .id(1L).seller(seller).pokemonId(25).pokemonName("pikachu")
                .price(500).status(ListingStatus.ACTIVE).build();

        when(marketListingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(2L)).thenReturn(Optional.of(buyer));

        assertThrows(BadRequestException.class, () -> marketService.buy(2L, 1L));
    }

    @Test
    void buy_shouldFail_whenBuyingOwnListing() {
        MarketListing listing = MarketListing.builder()
                .id(1L).seller(seller).pokemonId(25).pokemonName("pikachu")
                .price(300).status(ListingStatus.ACTIVE).build();

        when(marketListingRepository.findById(1L)).thenReturn(Optional.of(listing));

        assertThrows(BadRequestException.class, () -> marketService.buy(1L, 1L));
    }
}