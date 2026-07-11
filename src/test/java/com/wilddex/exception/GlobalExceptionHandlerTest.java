package com.wilddex.exception;

import com.wilddex.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/test");

    @Test
    void handleNotFound() {
        ResponseEntity<ErrorResponse> resp = handler.handleNotFound(
                new ResourceNotFoundException("Pokemon no encontrado"), request);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("NOT_FOUND", resp.getBody().code());
    }

    @Test
    void handleBadRequest() {
        ResponseEntity<ErrorResponse> resp = handler.handleBadRequest(
                new BadRequestException("Datos inválidos"), request);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("BAD_REQUEST", resp.getBody().code());
    }

    @Test
    void handleConflict() {
        ResponseEntity<ErrorResponse> resp = handler.handleConflict(
                new ConflictException("Ya existe"), request);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals("CONFLICT", resp.getBody().code());
    }

    @Test
    void handleUnauthorized() {
        ResponseEntity<ErrorResponse> resp = handler.handleUnauthorized(
                new UnauthorizedException("No autorizado"), request);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertEquals("UNAUTHORIZED", resp.getBody().code());
    }

    @Test
    void handleForbidden() {
        ResponseEntity<ErrorResponse> resp = handler.handleForbidden(
                new ForbiddenException("Prohibido"), request);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        assertEquals("FORBIDDEN", resp.getBody().code());
    }

    @Test
    void handleBadCredentials() {
        ResponseEntity<ErrorResponse> resp = handler.handleBadCredentials(
                new BadCredentialsException("bad creds"), request);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertEquals("BAD_CREDENTIALS", resp.getBody().code());
    }

    @Test
    void handleAccessDenied() {
        ResponseEntity<ErrorResponse> resp = handler.handleAccessDenied(
                new AccessDeniedException("denied"), request);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        assertEquals("ACCESS_DENIED", resp.getBody().code());
    }

    @Test
    void handleGeneric() {
        ResponseEntity<ErrorResponse> resp = handler.handleGeneric(
                new RuntimeException("algo falló"), request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("INTERNAL_ERROR", resp.getBody().code());
    }
}