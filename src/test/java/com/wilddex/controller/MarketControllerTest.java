package com.wilddex.controller;

import com.wilddex.dto.market.CreateListingRequest;
import com.wilddex.dto.market.MarketListingResponse;
import com.wilddex.dto.market.PurchaseResponse;
import com.wilddex.dto.response.ApiResponse;
import com.wilddex.model.AuthProvider;
import com.wilddex.model.Role;
import com.wilddex.model.User;
import com.wilddex.security.CustomUserDetails;
import com.wilddex.service.MarketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketControllerTest {

    @Mock private MarketService marketService;
    @InjectMocks private MarketController controller;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("ash").email("ash@pokemon.com")
                .password("enc").role(Role.USER).provider(AuthProvider.LOCAL).enabled(true).build();
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void publish_shouldReturn200() {
        when(marketService.publish(eq(1L), any())).thenReturn(null);
        CreateListingRequest req = new CreateListingRequest(25, "pikachu", 500);
        ResponseEntity<ApiResponse<MarketListingResponse>> resp = controller.publish(userDetails, req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void explore_shouldReturn200() {
        when(marketService.explore(any(), any())).thenReturn(new PageImpl<>(List.of()));
        ResponseEntity<ApiResponse<Page<MarketListingResponse>>> resp = controller.explore(null, Pageable.unpaged());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void buy_shouldReturn200() {
        when(marketService.buy(1L, 10L)).thenReturn(null);
        ResponseEntity<ApiResponse<PurchaseResponse>> resp = controller.buy(userDetails, 10L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void cancel_shouldReturn200() {
        ResponseEntity<ApiResponse<Void>> resp = controller.cancel(userDetails, 10L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(marketService).cancel(1L, 10L);
    }

    @Test
    void myListings_shouldReturn200() {
        when(marketService.myListings(1L)).thenReturn(List.of());
        ResponseEntity<ApiResponse<List<MarketListingResponse>>> resp = controller.myListings(userDetails);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void myPurchases_shouldReturn200() {
        when(marketService.myPurchases(1L)).thenReturn(List.of());
        ResponseEntity<ApiResponse<List<MarketListingResponse>>> resp = controller.myPurchases(userDetails);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}