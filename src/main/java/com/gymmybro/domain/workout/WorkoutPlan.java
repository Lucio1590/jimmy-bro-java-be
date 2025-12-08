package com.gymmybro.domain.workout;

import com.gymmybro.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * WorkoutPlan entity representing a training program created by a PT.
 */
@Entity
@Table(name = "workout_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * The PT who created this workout plan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    /**
     * Duration in weeks for this program
     */
    @Column(name = "duration_weeks")
    private Integer durationWeeks;

    /**
     * Difficulty level: BEGINNER, INTERMEDIATE, ADVANCED
     */
    @Column(name = "difficulty_level")
    private String difficultyLevel;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dayNumber ASC")
    @Builder.Default
    private List<WorkoutDay> workoutDays = new ArrayList<>();

    @OneToMany(mappedBy = "workoutPlan", fetch = FetchType.LAZY)
    @Builder.Default
    private List<WorkoutPlanAssignment> assignments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
