package com.gymmybro.application.service;

import com.gymmybro.application.dto.request.*;
import com.gymmybro.application.dto.response.*;
import com.gymmybro.domain.user.User;
import com.gymmybro.domain.user.UserRepository;
import com.gymmybro.domain.user.UserRole;
import com.gymmybro.domain.workout.*;
import com.gymmybro.exception.BadRequestException;
import com.gymmybro.exception.ForbiddenException;
import com.gymmybro.exception.ResourceNotFoundException;
import com.gymmybro.infrastructure.external.ExerciseDbClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for workout plan management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanAssignmentRepository assignmentRepository;
    private final ExerciseDbClient exerciseDbClient;
    private final UserRepository userRepository;

    /**
     * Create a new workout plan with nested days, blocks, and exercises.
     */
    @Transactional
    public WorkoutPlanResponse createWorkoutPlan(CreateWorkoutPlanRequest request, User creator) {
        log.info("Creating workout plan '{}' for user {}", request.getName(), creator.getId());

        WorkoutPlan plan = WorkoutPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .difficultyLevel(request.getDifficultyLevel())
                .durationWeeks(request.getDurationWeeks())
                .createdBy(creator)
                .isActive(true)
                .build();

        // Build nested structure
        if (request.getDays() != null) {
            for (CreateWorkoutDayRequest dayRequest : request.getDays()) {
                WorkoutDay day = buildWorkoutDay(dayRequest, plan);
                plan.getWorkoutDays().add(day);
            }
        }

        WorkoutPlan saved = workoutPlanRepository.save(plan);
        log.info("Created workout plan with ID {}", saved.getId());

        return WorkoutPlanResponse.fromEntity(saved);
    }

    /**
     * Get a workout plan by ID with full details.
     */
    @Transactional(readOnly = true)
    public WorkoutPlanResponse getWorkoutPlan(UUID planId) {
        WorkoutPlan plan = findPlanOrThrow(planId);
        WorkoutPlanResponse response = WorkoutPlanResponse.fromEntity(plan);
        response.setActiveAssignmentCount(assignmentRepository.countByWorkoutPlanIdAndIsActiveTrue(planId));
        return response;
    }

    /**
     * List workout plans with optional filters.
     */
    @Transactional(readOnly = true)
    public PaginatedResponse<WorkoutPlanSummaryResponse> getWorkoutPlans(
            UUID creatorId,
            String difficultyLevel,
            String search,
            Pageable pageable) {

        Page<WorkoutPlan> page = workoutPlanRepository.findByFilters(
                creatorId, difficultyLevel, search, pageable);

        List<WorkoutPlanSummaryResponse> content = page.getContent().stream()
                .map(plan -> {
                    WorkoutPlanSummaryResponse summary = WorkoutPlanSummaryResponse.fromEntity(plan);
                    summary.setActiveAssignmentCount(
                            assignmentRepository.countByWorkoutPlanIdAndIsActiveTrue(plan.getId()));
                    return summary;
                })
                .collect(Collectors.toList());

        return PaginatedResponse.<WorkoutPlanSummaryResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    /**
     * Get workout plans created by a specific PT.
     */
    @Transactional(readOnly = true)
    public PaginatedResponse<WorkoutPlanSummaryResponse> getMyWorkoutPlans(UUID creatorId, Pageable pageable) {
        Page<WorkoutPlan> page = workoutPlanRepository.findByCreatedByIdAndIsActiveTrue(creatorId, pageable);

        List<WorkoutPlanSummaryResponse> content = page.getContent().stream()
                .map(WorkoutPlanSummaryResponse::fromEntity)
                .collect(Collectors.toList());

        return PaginatedResponse.<WorkoutPlanSummaryResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    /**
     * Update a workout plan.
     */
    @Transactional
    public WorkoutPlanResponse updateWorkoutPlan(UUID planId, UpdateWorkoutPlanRequest request, User currentUser) {
        WorkoutPlan plan = findPlanOrThrow(planId);
        validateOwnership(plan, currentUser);

        log.info("Updating workout plan {}", planId);

        if (request.getName() != null) {
            plan.setName(request.getName());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getDifficultyLevel() != null) {
            plan.setDifficultyLevel(request.getDifficultyLevel());
        }
        if (request.getDurationWeeks() != null) {
            plan.setDurationWeeks(request.getDurationWeeks());
        }

        // If days are provided, replace existing days
        if (request.getDays() != null) {
            plan.getWorkoutDays().clear();
            for (CreateWorkoutDayRequest dayRequest : request.getDays()) {
                WorkoutDay day = buildWorkoutDay(dayRequest, plan);
                plan.getWorkoutDays().add(day);
            }
        }

        WorkoutPlan saved = workoutPlanRepository.save(plan);
        return WorkoutPlanResponse.fromEntity(saved);
    }

    /**
     * Soft delete (archive) a workout plan.
     */
    @Transactional
    public void deleteWorkoutPlan(UUID planId, User currentUser) {
        WorkoutPlan plan = findPlanOrThrow(planId);
        validateOwnership(plan, currentUser);

        log.info("Archiving workout plan {}", planId);
        plan.setActive(false);
        workoutPlanRepository.save(plan);
    }

    /**
     * Duplicate a workout plan for a new owner.
     */
    @Transactional
    public WorkoutPlanResponse duplicateWorkoutPlan(UUID planId, User newOwner) {
        WorkoutPlan original = findPlanOrThrow(planId);
        log.info("Duplicating workout plan {} for user {}", planId, newOwner.getId());

        WorkoutPlan copy = WorkoutPlan.builder()
                .name(original.getName() + " (Copy)")
                .description(original.getDescription())
                .difficultyLevel(original.getDifficultyLevel())
                .durationWeeks(original.getDurationWeeks())
                .createdBy(newOwner)
                .isActive(true)
                .build();

        // Deep copy days, blocks, and exercises
        for (WorkoutDay originalDay : original.getWorkoutDays()) {
            WorkoutDay copyDay = WorkoutDay.builder()
                    .workoutPlan(copy)
                    .dayNumber(originalDay.getDayNumber())
                    .name(originalDay.getName())
                    .description(originalDay.getDescription())
                    .isRestDay(originalDay.isRestDay())
                    .build();

            for (WorkoutBlock originalBlock : originalDay.getWorkoutBlocks()) {
                WorkoutBlock copyBlock = WorkoutBlock.builder()
                        .workoutDay(copyDay)
                        .blockOrder(originalBlock.getBlockOrder())
                        .blockType(originalBlock.getBlockType())
                        .name(originalBlock.getName())
                        .blockSets(originalBlock.getBlockSets())
                        .restBetweenRounds(originalBlock.getRestBetweenRounds())
                        .build();

                for (WorkoutExercise originalExercise : originalBlock.getWorkoutExercises()) {
                    WorkoutExercise copyExercise = WorkoutExercise.builder()
                            .workoutBlock(copyBlock)
                            .exerciseExternalId(originalExercise.getExerciseExternalId())
                            .exerciseName(originalExercise.getExerciseName())
                            .exerciseGifUrl(originalExercise.getExerciseGifUrl())
                            .exerciseOrder(originalExercise.getExerciseOrder())
                            .sets(originalExercise.getSets())
                            .targetReps(originalExercise.getTargetReps())
                            .targetWeight(originalExercise.getTargetWeight())
                            .restSeconds(originalExercise.getRestSeconds())
                            .durationSeconds(originalExercise.getDurationSeconds())
                            .tempo(originalExercise.getTempo())
                            .notes(originalExercise.getNotes())
                            .build();
                    copyBlock.getWorkoutExercises().add(copyExercise);
                }
                copyDay.getWorkoutBlocks().add(copyBlock);
            }
            copy.getWorkoutDays().add(copyDay);
        }

        WorkoutPlan saved = workoutPlanRepository.save(copy);
        log.info("Created duplicate workout plan with ID {}", saved.getId());
        return WorkoutPlanResponse.fromEntity(saved);
    }

    /**
     * Assign a workout plan to a trainee.
     */
    @Transactional
    public WorkoutAssignmentResponse assignPlanToTrainee(
            UUID planId, AssignWorkoutPlanRequest request, User assigner) {

        WorkoutPlan plan = findPlanOrThrow(planId);
        validateOwnership(plan, assigner);

        User trainee = userRepository.findById(request.getTraineeId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        if (trainee.getRole() != UserRole.TRAINEE) {
            throw new BadRequestException("User is not a trainee");
        }

        // Check if already assigned
        if (assignmentRepository.existsByTraineeIdAndWorkoutPlanId(trainee.getId(), planId)) {
            throw new BadRequestException("Trainee is already assigned to this plan");
        }

        // Deactivate any existing active assignment for this trainee
        assignmentRepository.findByTraineeIdAndIsActiveTrue(trainee.getId())
                .ifPresent(existing -> {
                    existing.setActive(false);
                    assignmentRepository.save(existing);
                });

        WorkoutPlanAssignment assignment = WorkoutPlanAssignment.builder()
                .trainee(trainee)
                .workoutPlan(plan)
                .assignedBy(assigner)
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .endDate(request.getEndDate())
                .isActive(true)
                .build();

        WorkoutPlanAssignment saved = assignmentRepository.save(assignment);
        log.info("Assigned plan {} to trainee {}", planId, trainee.getId());

        return WorkoutAssignmentResponse.fromEntity(saved);
    }

    /**
     * Unassign a workout plan from a trainee.
     */
    @Transactional
    public void unassignPlanFromTrainee(UUID planId, UUID traineeId, User currentUser) {
        WorkoutPlan plan = findPlanOrThrow(planId);
        validateOwnership(plan, currentUser);

        WorkoutPlanAssignment assignment = assignmentRepository
                .findByTraineeIdAndIsActiveTrue(traineeId)
                .filter(a -> a.getWorkoutPlan().getId().equals(planId))
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        log.info("Unassigning plan {} from trainee {}", planId, traineeId);
        assignment.setActive(false);
        assignmentRepository.save(assignment);
    }

    /**
     * Get all assignments for a workout plan.
     */
    @Transactional(readOnly = true)
    public List<WorkoutAssignmentResponse> getPlanAssignments(UUID planId) {
        findPlanOrThrow(planId);
        return assignmentRepository.findByWorkoutPlanId(planId).stream()
                .map(WorkoutAssignmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ==================== Private Helper Methods ====================

    private WorkoutPlan findPlanOrThrow(UUID planId) {
        return workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found"));
    }

    private void validateOwnership(WorkoutPlan plan, User currentUser) {
        if (currentUser.getRole() == UserRole.ADMIN) {
            return; // Admin can modify any plan
        }
        if (!plan.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You don't have permission to modify this workout plan");
        }
    }

    private WorkoutDay buildWorkoutDay(CreateWorkoutDayRequest request, WorkoutPlan plan) {
        WorkoutDay day = WorkoutDay.builder()
                .workoutPlan(plan)
                .dayNumber(request.getDayNumber())
                .name(request.getName())
                .description(request.getDescription())
                .isRestDay(request.isRestDay())
                .build();

        if (!request.isRestDay() && request.getBlocks() != null) {
            for (CreateWorkoutBlockRequest blockRequest : request.getBlocks()) {
                WorkoutBlock block = buildWorkoutBlock(blockRequest, day);
                day.getWorkoutBlocks().add(block);
            }
        }

        return day;
    }

    private WorkoutBlock buildWorkoutBlock(CreateWorkoutBlockRequest request, WorkoutDay day) {
        WorkoutBlock block = WorkoutBlock.builder()
                .workoutDay(day)
                .blockOrder(request.getBlockOrder())
                .blockType(request.getBlockType() != null ? request.getBlockType() : "STANDARD")
                .build();

        if (request.getExercises() != null) {
            for (CreateWorkoutExerciseRequest exerciseRequest : request.getExercises()) {
                WorkoutExercise exercise = buildWorkoutExercise(exerciseRequest, block);
                block.getWorkoutExercises().add(exercise);
            }
        }

        return block;
    }

    private WorkoutExercise buildWorkoutExercise(CreateWorkoutExerciseRequest request, WorkoutBlock block) {
        // Fetch exercise from ExerciseDB API to validate and get details
        ExerciseDbApiResponse apiResponse = exerciseDbClient.getExerciseById(request.getExerciseExternalId());

        if (apiResponse == null) {
            throw new ResourceNotFoundException(
                    "Exercise not found in ExerciseDB with ID: " + request.getExerciseExternalId());
        }

        return WorkoutExercise.builder()
                .workoutBlock(block)
                .exerciseExternalId(request.getExerciseExternalId())
                .exerciseName(apiResponse.getName())
                .exerciseGifUrl(apiResponse.getGifUrl())
                .exerciseOrder(request.getExerciseOrder())
                .sets(request.getSets())
                .targetReps(request.getReps() != null ? String.valueOf(request.getReps()) : null)
                .restSeconds(request.getRestSeconds())
                .durationSeconds(request.getDurationSeconds())
                .notes(request.getNotes())
                .build();
    }
}
