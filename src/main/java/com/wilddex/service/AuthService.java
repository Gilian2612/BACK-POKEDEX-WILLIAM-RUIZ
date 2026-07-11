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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Servicio de autenticación: registro, login con OTP, verificación OTP, OAuth2.
 * PKX-001 (registro), PKX-002 (login), PKX-003 (logout vía frontend/invalidación).
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;
    private final UserMapper userMapper;

    @Value("${app.otp.expiration-minutes}")
    private int otpExpirationMinutes;

    @Value("${app.otp.length}")
    private int otpLength;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       JavaMailSender mailSender,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailSender = mailSender;
        this.userMapper = userMapper;
    }

    /**
     * PKX-001: Registro de usuario local.
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("El correo electrónico ya está registrado");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("El nombre de usuario ya está en uso");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .provider(AuthProvider.LOCAL)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);
        logger.info("Usuario registrado exitosamente: {}", user.getEmail());

        // Enviar OTP de verificación de email
        sendOtp(user);

        return userMapper.toResponse(user);
    }

    /**
     * PKX-002: Login — valida credenciales y envía OTP por email.
     * Retorna mensaje indicando que se envió el código.
     */
    public void login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        sendOtp(user);
        logger.info("OTP enviado para login de: {}", user.getEmail());
    }

    /**
     * PKX-002: Verificar OTP y emitir tokens JWT.
     */
    @Transactional
    public AuthResponse verifyOtp(String email, String otpCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (user.getOtpCode() == null || user.getOtpExpiry() == null) {
            throw new BadRequestException("No hay código OTP pendiente");
        }
        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new BadRequestException("El código OTP ha expirado");
        }
        if (!user.getOtpCode().equals(otpCode)) {
            throw new BadRequestException("Código OTP incorrecto");
        }

        // Limpiar OTP y marcar email verificado
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        user.setEmailVerified(true);
        userRepository.save(user);

        // Generar tokens
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateTokenFromUserDetails(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        logger.info("Login exitoso con OTP para: {}", email);
        return new AuthResponse(accessToken, refreshToken, userMapper.toResponse(user));
    }

    /**
     * Procesa usuario OAuth2 (Google): crea o actualiza y retorna tokens.
     */
    @Transactional
    public AuthResponse processOAuth2User(String email, String name, String picture, String providerId) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Crear nuevo usuario desde Google
            String username = generateUniqueUsername(name);
            user = User.builder()
                    .username(username)
                    .email(email)
                    .provider(AuthProvider.GOOGLE)
                    .providerId(providerId)
                    .profileImageUrl(picture)
                    .emailVerified(true)
                    .build();
            user = userRepository.save(user);
            logger.info("Nuevo usuario OAuth2 registrado: {}", email);
        } else {
            // Actualizar datos de Google
            user.setProfileImageUrl(picture);
            user.setProviderId(providerId);
            if (user.getProvider() == AuthProvider.LOCAL) {
                user.setProvider(AuthProvider.GOOGLE);
            }
            userRepository.save(user);
            logger.info("Usuario OAuth2 actualizado: {}", email);
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateTokenFromUserDetails(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, userMapper.toResponse(user));
    }

    /**
     * Reenviar OTP al usuario.
     */
    @Transactional
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email no registrado"));
        sendOtp(user);
        logger.info("OTP reenviado a: {}", email);
    }

    // ── Helpers privados ──

    private void sendOtp(User user) {
        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        userRepository.save(user);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(user.getEmail());
            message.setSubject("WildDex - Código de verificación");
            message.setText("Tu código de verificación es: " + otp
                    + "\nEste código expira en " + otpExpirationMinutes + " minutos.");
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Error al enviar OTP por email a {}: {}", user.getEmail(), e.getMessage());
            // No lanzar excepción para no bloquear el flujo en desarrollo
        }
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private String generateUniqueUsername(String name) {
        String base = name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (base.length() < 4) base = base + "user";
        String username = base;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + counter++;
        }
        return username;
    }
}
