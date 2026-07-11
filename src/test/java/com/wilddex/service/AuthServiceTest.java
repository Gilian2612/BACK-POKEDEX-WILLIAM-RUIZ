package com.wilddex.service;

import com.wilddex.dto.request.LoginRequest;
import com.wilddex.dto.request.RegisterRequest;
import com.wilddex.dto.response.AuthResponse;
import com.wilddex.dto.response.UserResponse;
import com.wilddex.exception.BadRequestException;
import com.wilddex.exception.ConflictException;
import com.wilddex.exception.UnauthorizedException;
import com.wilddex.mapper.UserMapper;
import com.wilddex.model.AuthProvider;
import com.wilddex.model.User;
import com.wilddex.repository.UserRepository;
import com.wilddex.security.CustomUserDetails;
import com.wilddex.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private JavaMailSender mailSender;
    @Mock private UserMapper userMapper;

    @InjectMocks private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "otpExpirationMinutes", 5);
        ReflectionTestUtils.setField(authService, "otpLength", 6);
        ReflectionTestUtils.setField(authService, "mailFrom", "test@wilddex.com");

        user = User.builder()
                .id(1L).username("ash").email("ash@pokemon.com")
                .password("encoded").provider(AuthProvider.LOCAL)
                .emailVerified(false)
                .build();
    }

    // ── Register ──

    @Test
    void register_shouldSucceed() {
        RegisterRequest req = new RegisterRequest("ash", "ash@pokemon.com", "Pass123!", "Pass123!");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(userMapper.toResponse(any(User.class))).thenReturn(null);

        assertDoesNotThrow(() -> authService.register(req));
        verify(userRepository, atLeast(1)).save(any(User.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void register_shouldFail_whenPasswordsMismatch() {
        RegisterRequest req = new RegisterRequest("ash", "ash@pokemon.com", "Pass123!", "Different!");
        assertThrows(BadRequestException.class, () -> authService.register(req));
    }

    @Test
    void register_shouldFail_whenEmailExists() {
        RegisterRequest req = new RegisterRequest("ash", "ash@pokemon.com", "Pass123!", "Pass123!");
        when(userRepository.existsByEmail("ash@pokemon.com")).thenReturn(true);
        assertThrows(ConflictException.class, () -> authService.register(req));
    }

    @Test
    void register_shouldFail_whenUsernameExists() {
        RegisterRequest req = new RegisterRequest("ash", "ash@pokemon.com", "Pass123!", "Pass123!");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("ash")).thenReturn(true);
        assertThrows(ConflictException.class, () -> authService.register(req));
    }

    // ── Login ──

    @Test
    void login_shouldSendOtp() {
        LoginRequest req = new LoginRequest("ash@pokemon.com", "Pass123!");
        Authentication auth = mock(Authentication.class);
        CustomUserDetails details = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(details);
        when(userRepository.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> authService.login(req));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void login_shouldFail_whenUserNotFound() {
        LoginRequest req = new LoginRequest("nobody@pokemon.com", "Pass123!");
        Authentication auth = mock(Authentication.class);
        User tempUser = User.builder().id(99L).email("nobody@pokemon.com").build();
        CustomUserDetails details = new CustomUserDetails(tempUser);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(details);
        when(userRepository.findByEmail("nobody@pokemon.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }

    // ── Verify OTP ──

    @Test
    void verifyOtp_shouldReturnTokens_whenValid() {
        user.setOtpCode("123456");
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        when(userRepository.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateTokenFromUserDetails(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");
        when(userMapper.toResponse(any(User.class))).thenReturn(null);

        AuthResponse response = authService.verifyOtp("ash@pokemon.com", "123456");

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertNull(user.getOtpCode());
        assertTrue(user.isEmailVerified());
    }

    @Test
    void verifyOtp_shouldFail_whenNoOtpPending() {
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        when(userRepository.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> authService.verifyOtp("ash@pokemon.com", "123456"));
    }

    @Test
    void verifyOtp_shouldFail_whenExpired() {
        user.setOtpCode("123456");
        user.setOtpExpiry(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> authService.verifyOtp("ash@pokemon.com", "123456"));
    }

    @Test
    void verifyOtp_shouldFail_whenWrongCode() {
        user.setOtpCode("123456");
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        when(userRepository.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> authService.verifyOtp("ash@pokemon.com", "999999"));
    }

    @Test
    void verifyOtp_shouldFail_whenUserNotFound() {
        when(userRepository.findByEmail("nobody@pokemon.com")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> authService.verifyOtp("nobody@pokemon.com", "123456"));
    }

    // ── OAuth2 ──

    @Test
    void processOAuth2User_shouldCreateNewUser() {
        when(userRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(jwtTokenProvider.generateTokenFromUserDetails(any())).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh");
        when(userMapper.toResponse(any())).thenReturn(null);

        AuthResponse response = authService.processOAuth2User("new@gmail.com", "Misty", "pic.jpg", "google123");

        assertEquals("token", response.accessToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void processOAuth2User_shouldUpdateExistingUser() {
        when(userRepository.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateTokenFromUserDetails(any())).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh");
        when(userMapper.toResponse(any())).thenReturn(null);

        AuthResponse response = authService.processOAuth2User("ash@pokemon.com", "Ash", "new-pic.jpg", "google456");

        assertEquals("new-pic.jpg", user.getProfileImageUrl());
        assertEquals(AuthProvider.GOOGLE, user.getProvider());
    }

    // ── Resend OTP ──

    @Test
    void resendOtp_shouldSendNewOtp() {
        when(userRepository.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> authService.resendOtp("ash@pokemon.com"));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void resendOtp_shouldFail_whenEmailNotFound() {
        when(userRepository.findByEmail("nobody@pokemon.com")).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> authService.resendOtp("nobody@pokemon.com"));
    }
}