package com.wilddex.service;
import com.wilddex.dto.admin.AdminStatsResponse;
import com.wilddex.dto.admin.AdminUserResponse;
import com.wilddex.exception.BadRequestException;
import com.wilddex.exception.ResourceNotFoundException;
import com.wilddex.model.ListingStatus;
import com.wilddex.model.Role;
import com.wilddex.model.User;
import com.wilddex.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final CapturedPokemonRepository capturedPokemonRepository;
    private final FavoritePokemonRepository favoritePokemonRepository;
    private final TeamRepository teamRepository;
    private final MarketListingRepository marketListingRepository;

    public AdminService(UserRepository userRepository,
                        CapturedPokemonRepository capturedPokemonRepository,
                        FavoritePokemonRepository favoritePokemonRepository,
                        TeamRepository teamRepository,
                        MarketListingRepository marketListingRepository) {
        this.userRepository = userRepository;
        this.capturedPokemonRepository = capturedPokemonRepository;
        this.favoritePokemonRepository = favoritePokemonRepository;
        this.teamRepository = teamRepository;
        this.marketListingRepository = marketListingRepository;
    }

    /** ADM-001: Listar todos los usuarios con paginación. */
    public Page<AdminUserResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toAdminResponse);
    }

    /** ADM-002: Habilitar o deshabilitar un usuario. */
    @Transactional
    public AdminUserResponse toggleUserEnabled(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);

        log.info("Usuario {} {} por admin", user.getUsername(),
                user.isEnabled() ? "habilitado" : "deshabilitado");

        return toAdminResponse(user);
    }

    /** ADM-003: Cambiar el rol de un usuario. */
    @Transactional
    public AdminUserResponse updateRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        try {
            Role newRole = Role.valueOf(roleName.toUpperCase());
            user.setRole(newRole);
            userRepository.save(user);
            log.info("Rol de {} cambiado a {}", user.getUsername(), newRole);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol inválido: " + roleName + ". Valores permitidos: USER, ADMIN");
        }

        return toAdminResponse(user);
    }

    /** ADM-004: Estadísticas globales del sistema. */
    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalCaptures = capturedPokemonRepository.count();
        long totalTeams = teamRepository.count();
        long totalListings = marketListingRepository.count();
        long activeListings = marketListingRepository.findByStatus(ListingStatus.ACTIVE,
                Pageable.unpaged()).getTotalElements();

        // Pokémon más capturado y más favorito (top 1 por count)
        String mostCaptured = capturedPokemonRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        cp -> cp.getPokemonName(), java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("N/A");

        String mostFavorited = favoritePokemonRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        fp -> fp.getPokemonName(), java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("N/A");

        return new AdminStatsResponse(
                totalUsers, totalCaptures, totalTeams,
                totalListings, activeListings,
                mostCaptured, mostFavorited);
    }

    private AdminUserResponse toAdminResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getProvider().name(),
                user.isEnabled(),
                user.isEmailVerified(),
                user.getCoins(),
                user.getCreatedAt());
    }
}