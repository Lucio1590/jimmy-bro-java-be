package com.gymmybro.domain.workout;

import com.gymmybro.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * WorkoutLog entity representing a completed workout session by a trainee.
 */
@Entity
@Table(name = "workout_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_day_id", nullable = false)
    private WorkoutDay workoutDay;

    /**
     * Date when the workout was performed
     */
    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    /**
     * When the workout session started
     */
    @Column(name = "started_at")
    private Instant startedAt;

    /**
     * When the workout session ended
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Duration in minutes
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /**
     * Overall notes for the session
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Rating from 1-10 for how the workout felt
     */
    private Integer rating;

    @OneToMany(mappedBy = "workoutLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkoutLogEntry> entries = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
