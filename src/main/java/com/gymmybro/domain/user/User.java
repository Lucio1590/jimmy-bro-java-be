package com.gymmybro.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User entity representing all user accounts in the system.
 * Implements a self-referential relationship for PT → Trainee hierarchy.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "email_verified")
    @Builder.Default
    private boolean emailVerified = false;

    /**
     * Self-referential relationship: A trainee belongs to a PT.
     * Only applicable when role = TRAINEE
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id")
    private User personalTrainer;

    /**
     * Self-referential relationship: A PT has many trainees.
     * Only applicable when role = PT
     */
    @OneToMany(mappedBy = "personalTrainer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> trainees = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
