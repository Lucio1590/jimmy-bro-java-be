package com.gymmybro.application.service;

import com.gymmybro.application.dto.request.LoginRequest;
import com.gymmybro.application.dto.request.RefreshTokenRequest;
import com.gymmybro.application.dto.request.RegisterRequest;
import com.gymmybro.application.dto.response.TokenResponse;
import com.gymmybro.application.dto.response.UserResponse;
import com.gymmybro.config.JwtConfig;
import com.gymmybro.domain.token.RefreshToken;
import com.gymmybro.domain.token.RefreshTokenRepository;

import com.gymmybro.domain.user.User;
import com.gymmybro.domain.user.UserRepository;
import com.gymmybro.exception.BadRequestException;
import com.gymmybro.exception.ConflictException;
import com.gymmybro.exception.UnauthorizedException;
import com.gymmybro.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * Service for authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User", "email", request.getEmail());
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .hashedPassword(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .isActive(true)
                .emailVerified(false) // Requires email confirmation
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered: {} with role {}", savedUser.getEmail(), savedUser.getRole());

        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Authenticate user and generate tokens
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // Authenticate with Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()));

        // Get user
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        // Generate tokens
        return generateTokens(user);
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token format
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        // Check if token is in database and not revoked
        String tokenHash = hashToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        if (storedToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        // Get user
        User user = storedToken.getUser();
        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        // Revoke old refresh token
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        // Generate new tokens
        return generateTokens(user);
    }

    /**
     * Logout user - revoke tokens
     */
    @Transactional
    public void logout(String accessToken) {
        try {

            // Revoke all refresh tokens for this user
            UUID userId = jwtService.extractUserId(accessToken);
            refreshTokenRepository.revokeAllByUserId(userId);

            log.info("User logged out: {}", jwtService.extractUsername(accessToken));
        } catch (Exception e) {
            log.warn("Error during logout: {}", e.getMessage());
            // Don't throw - logout should always succeed from user perspective
        }
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return UserResponse.fromEntity(user);
    }

    /**
     * Generate access and refresh tokens for user
     */
    private TokenResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Store refresh token hash in database
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(jwtService.getRefreshTokenExpiration())
                .isRevoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        long expiresInSeconds = jwtConfig.getAccessExpirationMinutes() * 60L;
        return TokenResponse.of(accessToken, refreshToken, expiresInSeconds);
    }

    /**
     * Hash token for secure storage
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new BadRequestException("Error hashing token");
        }
    }
}
