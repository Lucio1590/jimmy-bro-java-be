package com.gymmybro.infrastructure;

import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import com.gymmybro.domain.user.*;
import com.gymmybro.domain.workout.*;
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
 * 
 * Uses preconfigured exercise data from ExerciseDB RapidAPI to avoid
 * making API calls during application startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanAssignmentRepository assignmentRepository;
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
                List<PersonalTrainer> pts = seedPersonalTrainers();
                List<Trainee> trainees = seedTrainees(pts);

                // Use preconfigured exercises instead of API calls
                Map<String, ExerciseDbApiResponse> exercises = getPreconfiguredExercises();

                if (!exercises.isEmpty()) {
                    List<WorkoutPlan> plans = seedWorkoutPlans(pts.get(0), exercises);
                    seedAssignments(plans, trainees, pts.get(0));
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
        User admin = Admin.builder()
                .email(adminEmail)
                .hashedPassword(passwordEncoder.encode(adminPassword))
                .fullName("System Administrator")
                // .role(UserRole.ADMIN) // Role is set by discriminator
                .isActive(true)
                .emailVerified(true)
                .build();

        userRepository.save(admin);
        log.info("Created default admin user: {}", admin.getEmail());
        return admin;
    }

        private List<PersonalTrainer> seedPersonalTrainers() {
                List<PersonalTrainer> pts = new ArrayList<>();

        PersonalTrainer pt1 = PersonalTrainer.builder()
                .email("pt1@gymmybro.com")
                .hashedPassword(passwordEncoder.encode("password"))
                .fullName("Mike Mentzer")
                .isActive(true)
                .emailVerified(true)
                .build();

        PersonalTrainer pt2 = PersonalTrainer.builder()
                .email("pt2@gymmybro.com")
                .hashedPassword(passwordEncoder.encode("password"))
                .fullName("Arnold S")
                .isActive(true)
                .emailVerified(true)
                .build();

        pts.add(userRepository.save(pt1));
        pts.add(userRepository.save(pt2));
        log.info("Created 2 Personal Trainers");
        return pts;
    }

        private List<Trainee> seedTrainees(List<PersonalTrainer> pts) {
                List<Trainee> trainees = new ArrayList<>();

                // Map trainees to trainers: trainee1, trainee4 -> pt1; trainee2, trainee3, trainee5 -> pt2
                String[][] traineeMappings = new String[][] {
                                {"trainee1@gymmybro.com", "Trainee 1", "0"},
                                {"trainee2@gymmybro.com", "Trainee 2", "1"},
                                {"trainee3@gymmybro.com", "Trainee 3", "1"},
                                {"trainee4@gymmybro.com", "Trainee 4", "0"},
                                {"trainee5@gymmybro.com", "Trainee 5", "1"}
                };

                for (String[] t : traineeMappings) {
                        int ptIndex = Integer.parseInt(t[2]);
                        PersonalTrainer trainer = pts.get(ptIndex);

                        Trainee trainee = Trainee.builder()
                                        .email(t[0])
                                        .hashedPassword(passwordEncoder.encode("password"))
                                        .fullName(t[1])
                                        .isActive(true)
                                        .emailVerified(true)
                                        .personalTrainer(trainer)
                                        .build();

                        trainees.add(userRepository.save(trainee));

                        // Maintain bidirectional link if the collection is present
                        if (trainer.getTrainees() != null) {
                                trainer.getTrainees().add(trainee);
                        }
                }

                log.info("Created {} Trainees and linked them to trainers", trainees.size());
                return trainees;
        }

    /**
     * Returns preconfigured exercises with real ExerciseDB IDs.
     * These IDs are from the ExerciseDB RapidAPI (exercisedb-api1.p.rapidapi.com).
     * Using preconfigured data avoids API calls during application startup.
     */
    private Map<String, ExerciseDbApiResponse> getPreconfiguredExercises() {
        log.info("Loading preconfigured exercises...");
        Map<String, ExerciseDbApiResponse> exercises = new HashMap<>();

        // Bench Press - Barbell chest exercise
        exercises.put("bench press", ExerciseDbApiResponse.builder()
                .id("exr_41n2hxnFMotsXTj3")
                .name("Bench Press")
                .imageUrl("https://cdn.exercisedb.dev/media/w/images/A8OLBqBa26.jpg")
                .bodyParts(List.of("CHEST"))
                .equipments(List.of("BARBELL"))
                .targetMuscles(List.of("PECTORALIS MAJOR STERNAL HEAD"))
                .secondaryMuscles(List.of("TRICEPS BRACHII", "ANTERIOR DELTOID", "PECTORALIS MAJOR CLAVICULAR HEAD"))
                .instructions(List.of(
                        "Grip the barbell with your hands slightly wider than shoulder-width apart, palms facing your feet, and lift it off the rack, holding it straight over your chest with your arms fully extended.",
                        "Slowly lower the barbell down to your chest while keeping your elbows at a 90-degree angle.",
                        "Once the barbell touches your chest, push it back up to the starting position while keeping your back flat on the bench.",
                        "Repeat this process for the desired number of repetitions, always maintaining control of the barbell and ensuring your form is correct."))
                .build());

        // Goblet Squat - Dumbbell squat variation (best match for "barbell squat" search)
        exercises.put("squat", ExerciseDbApiResponse.builder()
                .id("exr_41n2hQDiSwTZXM4F")
                .name("Goblet Squat")
                .imageUrl("https://cdn.exercisedb.dev/media/w/images/lwT5fwjbAU.jpg")
                .bodyParts(List.of("THIGHS"))
                .equipments(List.of("DUMBBELL"))
                .targetMuscles(List.of("QUADRICEPS", "HAMSTRINGS", "GLUTEUS MAXIMUS"))
                .secondaryMuscles(List.of("ERECTOR SPINAE", "ADDUCTOR MAGNUS", "GASTROCNEMIUS"))
                .instructions(List.of(
                        "Engage your core and keep your chest up, then start to lower your body into a squat position by bending your knees and pushing your hips back.",
                        "Continue lowering yourself until your hips are below your knees, making sure your elbows are inside your knees at the bottom of the squat.",
                        "Pause for a moment at the bottom of the squat, then push through your heels to stand back up to the starting position.",
                        "Repeat this movement for the desired number of repetitions, making sure to maintain proper form throughout the exercise."))
                .build());

        // Romanian Deadlift - Hip hinge movement
        exercises.put("deadlift", ExerciseDbApiResponse.builder()
                .id("exr_41n2hn8rpbYihzEW")
                .name("Romanian Deadlift")
                .imageUrl("https://cdn.exercisedb.dev/media/w/images/3wgSOkOkH5.jpg")
                .bodyParts(List.of("HIPS"))
                .equipments(List.of("DUMBBELL"))
                .targetMuscles(List.of("HAMSTRINGS", "GLUTEUS MAXIMUS"))
                .secondaryMuscles(List.of("QUADRICEPS", "SOLEUS"))
                .instructions(List.of(
                        "Keep your back straight and your shoulders back as you begin to bend at the hips, pushing them back while you lower the barbell along the front of your legs.",
                        "Continue lowering the barbell until it reaches mid-shin level, or until you feel a stretch in your hamstrings, making sure to keep the barbell close to your body throughout the movement.",
                        "After reaching this position, pause for a moment, and then slowly reverse the movement by driving your hips forward and standing back up to the starting position, squeezing your glutes at the top.",
                        "Remember to keep your core engaged and your back straight throughout the entire exercise to avoid injury."))
                .build());

        // Pull up - Back exercise
        exercises.put("pull up", ExerciseDbApiResponse.builder()
                .id("exr_41n2hU4y6EaYXFhr")
                .name("Pull up")
                .imageUrl("https://cdn.exercisedb.dev/media/w/images/gNdMNPVxAj.jpg")
                .bodyParts(List.of("BACK"))
                .equipments(List.of("BODY WEIGHT"))
                .targetMuscles(List.of("LATISSIMUS DORSI"))
                .secondaryMuscles(List.of("TERES MAJOR", "BRACHIALIS", "BRACHIORADIALIS", "TRAPEZIUS LOWER FIBERS"))
                .instructions(List.of(
                        "With a firm grip on the bar, pull your shoulder blades down and back, bend your legs at the knees if necessary, and cross your ankles.",
                        "Engage your core and pull your body up until your chin is above the bar while keeping your elbows close to your body.",
                        "Hold this position for a moment, ensuring your chest is close or touching the bar.",
                        "Slowly lower your body back down to the starting position, fully extending your arms and maintaining control throughout the movement."))
                .build());

        // Push-up - Chest bodyweight exercise
        exercises.put("push up", ExerciseDbApiResponse.builder()
                .id("exr_41n2hNXJadYcfjnd")
                .name("Push-up")
                .imageUrl("https://cdn.exercisedb.dev/media/w/images/cuG10rd4ea.jpg")
                .bodyParts(List.of("CHEST"))
                .equipments(List.of("BODY WEIGHT"))
                .targetMuscles(List.of("PECTORALIS MAJOR STERNAL HEAD"))
                .secondaryMuscles(List.of("TRICEPS BRACHII", "ANTERIOR DELTOID", "PECTORALIS MAJOR CLAVICULAR HEAD"))
                .instructions(List.of(
                        "Lower your body until your chest is close to the floor, keeping your back straight and your elbows close to your body.",
                        "Push your body up, extending your arms fully but without locking your elbows, while maintaining your body in a straight line.",
                        "Pause for a moment at the top of the push-up.",
                        "Lower your body back down to the starting position, ensuring you don't drop your body too quickly, and repeat the exercise."))
                .build());

        // Cross Body Hammer Curl - Arm exercise
        exercises.put("dumbbell curl", ExerciseDbApiResponse.builder()
                .id("exr_41n2hgCHNgtVLHna")
                .name("Cross Body Hammer Curl")
                .imageUrl("https://cdn.exercisedb.dev/media/w/images/6vI4gkByYk.jpg")
                .bodyParts(List.of("FOREARMS"))
                .equipments(List.of("DUMBBELL"))
                .targetMuscles(List.of("BRACHIORADIALIS"))
                .secondaryMuscles(List.of("BICEPS BRACHII", "BRACHIALIS"))
                .instructions(List.of(
                        "While keeping your upper arm stationary, use your biceps to curl the weight until the dumbbells are at shoulder level. Do this while keeping your palms facing your torso.",
                        "Hold the contracted position for a brief moment as you squeeze your biceps.",
                        "Slowly begin to bring the dumbbells back to the starting position.",
                        "Repeat the same steps for the desired amount of repetitions and then switch arms."))
                .build());

        log.info("Loaded {} preconfigured exercises", exercises.size());
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

        if (exercises.containsKey("squat")) {
            addExerciseToBlock(block1, exercises.get("squat"), 1, 5, "5", "90s");
        }
        if (exercises.containsKey("bench press")) {
            addExerciseToBlock(block1, exercises.get("bench press"), 2, 5, "5", "90s");
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

        if (exercises.containsKey("squat")) {
            addExerciseToBlock(block2, exercises.get("squat"), 1, 5, "5", "90s");
        }
        if (exercises.containsKey("deadlift")) {
            addExerciseToBlock(block2, exercises.get("deadlift"), 2, 1, "5", "120s");
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

        private void seedAssignments(List<WorkoutPlan> plans, List<Trainee> trainees, PersonalTrainer pt) {
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
