package com.gymmybro.domain.token;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * RevokedAccessToken entity for blacklisting JWT access tokens.
 * Used when a user logs out or when tokens need to be invalidated.
 */
@Entity
@Table(name = "revoked_access_tokens", indexes = {
        @Index(name = "idx_revoked_tokens_jti", columnList = "jti"),
        @Index(name = "idx_revoked_tokens_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * JWT ID (jti claim) of the revoked token
     */
    @Column(nullable = false, unique = true)
    private String jti;

    /**
     * When the original token was set to expire.
     * Used for cleanup - can delete entries after this time.
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /**
     * User who owned this token
     */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * Reason for revocation
     */
    @Column(name = "revocation_reason")
    private String revocationReason;

    @CreationTimestamp
    @Column(name = "revoked_at", updatable = false)
    private Instant revokedAt;
}
