package com.gymmybro.domain.token;

import com.gymmybro.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * RefreshToken entity for storing JWT refresh tokens.
 * Enables token refresh without requiring re-authentication.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The actual refresh token string (hashed)
     */
    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    /**
     * When this token expires
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /**
     * Whether this token has been revoked
     */
    @Column(name = "is_revoked")
    @Builder.Default
    private boolean isRevoked = false;

    /**
     * Device/client info for multi-device tracking
     */
    @Column(name = "device_info")
    private String deviceInfo;

    /**
     * IP address from which the token was created
     */
    @Column(name = "ip_address")
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
