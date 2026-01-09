package com.gymmybro.infrastructure;

import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import com.gymmybro.domain.user.User;
import com.gymmybro.domain.user.UserRepository;
import com.gymmybro.domain.user.UserRole;
import com.gymmybro.domain.workout.*;
import com.gymmybro.infrastructure.external.ExerciseDbClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * DataInitializer runs on application startup to seed the database
 * with initial data if it's empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanAssignmentRepository assignmentRepository;
    private final ExerciseDbClient exerciseDbClient;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@gymmybro.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // log.warn("Resetting database for development seeding...");
        // clearDatabase();

        if (userRepository.count() == 0) {
            log.info("Database is empty. Seeding initial data...");
            try {
                User admin = seedAdminUser();
                List<User> pts = seedPersonalTrainers();
                List<User> trainees = seedTrainees();

                Map<String, ExerciseDbApiResponse> exercises = fetchRealExercises();

                if (!exercises.isEmpty()) {
                    List<WorkoutPlan> plans = seedWorkoutPlans(pts.get(0), exercises);
                    seedAssignments(plans, trainees, pts.get(0));
                } else {
                    log.warn("Could not fetch exercises from ExerciseDB. Skipping workout plan seeding.");
                }

            } catch (Exception e) {
                log.error("Failed to seed data: {}", e.getMessage(), e);
            }
            log.info("Initial data seeding completed.");
        } else {
            log.info("Database already contains data. Skipping initialization.");
        }
    }

    private void clearDatabase() {
        assignmentRepository.deleteAll();
        workoutPlanRepository.deleteAll();
        userRepository.deleteAll();
        log.info("Database cleared.");
    }

    private User seedAdminUser() {
        User admin = User.builder()
                .email(adminEmail)
                .hashedPassword(passwordEncoder.encode(adminPassword))
                .fullName("System Administrator")
                .role(UserRole.ADMIN)
                .isActive(true)
                .emailVerified(true)
                .build();

        userRepository.save(admin);
        log.info("Created default admin user: {}", admin.getEmail());
        return admin;
    }

    private List<User> seedPersonalTrainers() {
        List<User> pts = new ArrayList<>();

        User pt1 = User.builder()
                .email("pt1@gymmybro.com")
                .hashedPassword(passwordEncoder.encode("password"))
                .fullName("Mike Mentzer")
                .role(UserRole.PT)
                .isActive(true)
                .emailVerified(true)
                .build();

        User pt2 = User.builder()
                .email("pt2@gymmybro.com")
                .hashedPassword(passwordEncoder.encode("password"))
                .fullName("Arnold S")
                .role(UserRole.PT)
                .isActive(true)
                .emailVerified(true)
                .build();

        pts.add(userRepository.save(pt1));
        pts.add(userRepository.save(pt2));
        log.info("Created 2 Personal Trainers");
        return pts;
    }

    private List<User> seedTrainees() {
        List<User> trainees = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            User trainee = User.builder()
                    .email("trainee" + i + "@gymmybro.com")
                    .hashedPassword(passwordEncoder.encode("password"))
                    .fullName("Trainee " + i)
                    .role(UserRole.TRAINEE)
                    .isActive(true)
                    .emailVerified(true)
                    .build();
            trainees.add(userRepository.save(trainee));
        }
        log.info("Created 3 Trainees");
        return trainees;
    }

    private Map<String, ExerciseDbApiResponse> fetchRealExercises() {
        log.info("Fetching real exercises from ExerciseDB...");
        Map<String, ExerciseDbApiResponse> exercises = new HashMap<>();

        // Common exercises to search for
        String[] searchTerms = { "barbell bench press", "barbell squat", "barbell deadlift", "pull up", "push up",
                "dumbbell curl" };

        for (String term : searchTerms) {
            try {
                var results = exerciseDbClient.searchByName(term);
                if (!results.isEmpty()) {
                    // Find the best match (shortest name usually closest to exact match)
                    ExerciseDbApiResponse bestMatch = results.stream()
                            .min(Comparator.comparingInt(a -> a.getName().length()))
                            .orElse(results.get(0));

                    exercises.put(term, bestMatch);
                    log.info("Found exercise: {} -> {}", term, bestMatch.getName());
                }
            } catch (Exception e) {
                log.warn("Failed to fetch exercise '{}': {}", term, e.getMessage());
            }
        }
        return exercises;
    }

    private List<WorkoutPlan> seedWorkoutPlans(User creator, Map<String, ExerciseDbApiResponse> exercises) {
        List<WorkoutPlan> plans = new ArrayList<>();

        // 1. Strength 5x5 Plan
        WorkoutPlan strengthPlan = WorkoutPlan.builder()
                .name("Strength 5x5 Foundation")
                .description("Classic strength building routine focussing on compound movements.")
                .difficultyLevel("BEGINNER")
                .durationWeeks(12)
                .createdBy(creator)
                .isActive(true)
                .build();

        WorkoutDay dayA = WorkoutDay.builder()
                .workoutPlan(strengthPlan)
                .dayNumber(1)
                .name("Workout A")
                .description("Squat, Bench, Row")
                .build();

        WorkoutBlock block1 = WorkoutBlock.builder()
                .workoutDay(dayA)
                .blockOrder(1)
                .blockType("STANDARD")
                .build();

        if (exercises.containsKey("barbell squat")) {
            addExerciseToBlock(block1, exercises.get("barbell squat"), 1, 5, "5", "90s");
        }
        if (exercises.containsKey("barbell bench press")) {
            addExerciseToBlock(block1, exercises.get("barbell bench press"), 2, 5, "5", "90s");
        }

        dayA.getWorkoutBlocks().add(block1);
        strengthPlan.getWorkoutDays().add(dayA);

        // Add minimal Day B for variety
        WorkoutDay dayB = WorkoutDay.builder()
                .workoutPlan(strengthPlan)
                .dayNumber(3)
                .name("Workout B")
                .description("Squat, Overhead Press, Deadlift")
                .build();

        WorkoutBlock block2 = WorkoutBlock.builder()
                .workoutDay(dayB)
                .blockOrder(1)
                .blockType("STANDARD")
                .build();

        if (exercises.containsKey("barbell squat")) {
            addExerciseToBlock(block2, exercises.get("barbell squat"), 1, 5, "5", "90s");
        }
        if (exercises.containsKey("barbell deadlift")) {
            addExerciseToBlock(block2, exercises.get("barbell deadlift"), 2, 1, "5", "120s");
        }

        dayB.getWorkoutBlocks().add(block2);
        strengthPlan.getWorkoutDays().add(dayB);

        plans.add(workoutPlanRepository.save(strengthPlan));
        log.info("Created 'Strength 5x5' plan");

        return plans;
    }

    private void addExerciseToBlock(WorkoutBlock block, ExerciseDbApiResponse apiExercise, int order, int sets,
            String reps, String rest) {
        WorkoutExercise exercise = WorkoutExercise.builder()
                .workoutBlock(block)
                .exerciseExternalId(apiExercise.getId())
                .exerciseName(apiExercise.getName())
                .exerciseGifUrl(apiExercise.getGifUrl())
                .exerciseOrder(order)
                .sets(sets)
                .targetReps(reps)
                .restSeconds(parseRestSeconds(rest))
                .build();
        block.getWorkoutExercises().add(exercise);
    }

    private int parseRestSeconds(String rest) {
        return Integer.parseInt(rest.replace("s", ""));
    }

    private void seedAssignments(List<WorkoutPlan> plans, List<User> trainees, User pt) {
        if (plans.isEmpty() || trainees.isEmpty())
            return;

        // Assign first plan to first trainee
        WorkoutPlan plan = plans.get(0);
        User trainee = trainees.get(0);

        WorkoutPlanAssignment assignment = WorkoutPlanAssignment.builder()
                .workoutPlan(plan)
                .trainee(trainee)
                .assignedBy(pt)
                .isActive(true)
                .startDate(LocalDate.now())
                .build();

        assignmentRepository.save(assignment);
        log.info("Assigned plan '{}' to trainee '{}'", plan.getName(), trainee.getEmail());
    }
}
