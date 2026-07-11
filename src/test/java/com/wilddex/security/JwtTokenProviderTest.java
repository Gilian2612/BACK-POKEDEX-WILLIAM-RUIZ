package com.wilddex.security;

import com.wilddex.model.AuthProvider;
import com.wilddex.model.Role;
import com.wilddex.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Secret must be at least 32 bytes for HMAC-SHA256
        String secret = "ThisIsATestSecretKeyForJwtToken32";
        jwtTokenProvider = new JwtTokenProvider(secret, 3600000, 86400000);

        User user = User.builder()
                .id(1L).username("ash").email("ash@pokemon.com")
                .password("encoded").role(Role.USER)
                .provider(AuthProvider.LOCAL).enabled(true)
                .build();
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void generateTokenFromUserDetails_shouldReturnValidToken() {
        String token = jwtTokenProvider.generateTokenFromUserDetails(userDetails);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void generateRefreshToken_shouldReturnValidToken() {
        String token = jwtTokenProvider.generateRefreshToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void getEmailFromToken_shouldReturnEmail() {
        String token = jwtTokenProvider.generateTokenFromUserDetails(userDetails);

        String email = jwtTokenProvider.getEmailFromToken(token);

        assertEquals("ash@pokemon.com", email);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsEmpty() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    void validateToken_shouldThrowOrReturnFalse_whenTokenSignedWithDifferentKey() {
        JwtTokenProvider otherProvider = new JwtTokenProvider("AnotherSecretKeyThatIsDifferent!!", 3600000, 86400000);
        String token = otherProvider.generateTokenFromUserDetails(userDetails);

        // SignatureException extends SecurityException, but jjwt may throw the subclass directly
        try {
            boolean result = jwtTokenProvider.validateToken(token);
            assertFalse(result);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // Also acceptable - signature mismatch detected
            assertNotNull(e);
        }
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenExpired() {
        // Create provider with 0ms expiration
        JwtTokenProvider expiredProvider = new JwtTokenProvider("ThisIsATestSecretKeyForJwtToken32", 0, 0);
        String token = expiredProvider.generateTokenFromUserDetails(userDetails);

        assertFalse(jwtTokenProvider.validateToken(token));
    }
}