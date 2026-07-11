package com.wilddex.controller;

import com.wilddex.dto.response.ApiResponse;
import com.wilddex.dto.response.CollectionItemResponse;
import com.wilddex.model.AuthProvider;
import com.wilddex.model.Role;
import com.wilddex.model.User;
import com.wilddex.security.CustomUserDetails;
import com.wilddex.service.CollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionControllerTest {

    @Mock private CollectionService collectionService;
    @InjectMocks private CollectionController controller;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("ash").email("ash@pokemon.com")
                .password("enc").role(Role.USER).provider(AuthProvider.LOCAL).enabled(true).build();
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void getCaptured_shouldReturnList() {
        when(collectionService.getCapturedPokemon(1L)).thenReturn(List.of());
        ResponseEntity<ApiResponse<List<CollectionItemResponse>>> resp = controller.getCaptured(userDetails);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void capture_shouldReturn201() {
        when(collectionService.capturePokemon(1L, 25, "pikachu")).thenReturn(null);
        ResponseEntity<ApiResponse<CollectionItemResponse>> resp = controller.capture(userDetails, 25, "pikachu");
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    void release_shouldReturn200() {
        ResponseEntity<ApiResponse<Void>> resp = controller.release(userDetails, 25);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(collectionService).releasePokemon(1L, 25);
    }

    @Test
    void getFavorites_shouldReturnList() {
        when(collectionService.getFavoritePokemon(1L)).thenReturn(List.of());
        ResponseEntity<ApiResponse<List<CollectionItemResponse>>> resp = controller.getFavorites(userDetails);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void addFavorite_shouldReturn201() {
        when(collectionService.addFavorite(1L, 25, "pikachu")).thenReturn(null);
        ResponseEntity<ApiResponse<CollectionItemResponse>> resp = controller.addFavorite(userDetails, 25, "pikachu");
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    void removeFavorite_shouldReturn200() {
        ResponseEntity<ApiResponse<Void>> resp = controller.removeFavorite(userDetails, 25);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(collectionService).removeFavorite(1L, 25);
    }
}