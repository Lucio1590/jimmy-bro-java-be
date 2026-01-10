package com.gymmybro.presentation.controller;

import com.gymmybro.application.dto.request.AssignWorkoutPlanRequest;
import com.gymmybro.application.dto.request.CreateWorkoutPlanRequest;
import com.gymmybro.application.dto.request.UpdateWorkoutPlanRequest;
import com.gymmybro.application.dto.response.*;
import com.gymmybro.application.service.WorkoutPlanService;
import com.gymmybro.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for workout plan management operations.
 */
@RestController
@RequestMapping("/api/v1/workout-plans")
@RequiredArgsConstructor
@Tag(name = "Workout Plans", description = "Workout plan CRUD and assignment operations")
@SecurityRequirement(name = "bearerAuth")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Create workout plan", description = "Create a new workout plan with days, blocks, and exercises. PT and ADMIN only.")
    @ApiResponse(responseCode = "201", description = "Workout plan created successfully")
    public ResponseEntity<WorkoutPlanResponse> createWorkoutPlan(
            @Valid @RequestBody CreateWorkoutPlanRequest request,
            @AuthenticationPrincipal User currentUser) {
        WorkoutPlanResponse response = workoutPlanService.createWorkoutPlan(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List workout plans", description = "Get paginated list of workout plans with optional filters")
    public ResponseEntity<PaginatedResponse<WorkoutPlanSummaryResponse>> getWorkoutPlans(
            @Parameter(description = "Filter by creator ID") @RequestParam(required = false) UUID creatorId,
            @Parameter(description = "Filter by difficulty level") @RequestParam(required = false) String difficultyLevel,
            @Parameter(description = "Search by name") @RequestParam(required = false) String search,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PaginatedResponse<WorkoutPlanSummaryResponse> response = workoutPlanService.getWorkoutPlans(creatorId,
                difficultyLevel, search, pageable);
        return ResponseEntity.ok(response);
    }

        @GetMapping({"/mine", "/my-plans"})
        @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
        @Operation(summary = "Get my workout plans", description = "Get workout plans created by the current user")
        public ResponseEntity<PaginatedResponse<WorkoutPlanSummaryResponse>> getMyWorkoutPlans(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PaginatedResponse<WorkoutPlanSummaryResponse> response = workoutPlanService
            .getMyWorkoutPlans(currentUser.getId(), pageable);
        return ResponseEntity.ok(response);
        }

    @GetMapping("/{id}")
    @Operation(summary = "Get workout plan", description = "Get workout plan with full details including days, blocks, and exercises")
    public ResponseEntity<WorkoutPlanResponse> getWorkoutPlan(
            @Parameter(description = "Workout plan ID") @PathVariable UUID id) {
        WorkoutPlanResponse response = workoutPlanService.getWorkoutPlan(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Update workout plan", description = "Update an existing workout plan. Only the owner or admin can update.")
    public ResponseEntity<WorkoutPlanResponse> updateWorkoutPlan(
            @Parameter(description = "Workout plan ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkoutPlanRequest request,
            @AuthenticationPrincipal User currentUser) {
        WorkoutPlanResponse response = workoutPlanService.updateWorkoutPlan(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Delete workout plan", description = "Archive a workout plan (soft delete). Only the owner or admin can delete.")
    @ApiResponse(responseCode = "204", description = "Workout plan deleted successfully")
    public ResponseEntity<Void> deleteWorkoutPlan(
            @Parameter(description = "Workout plan ID") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        workoutPlanService.deleteWorkoutPlan(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Duplicate workout plan", description = "Create a copy of an existing workout plan")
    @ApiResponse(responseCode = "201", description = "Workout plan duplicated successfully")
    public ResponseEntity<WorkoutPlanResponse> duplicateWorkoutPlan(
            @Parameter(description = "Workout plan ID to duplicate") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        WorkoutPlanResponse response = workoutPlanService.duplicateWorkoutPlan(id, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Assign workout plan to trainee", description = "Assign a workout plan to a trainee. Deactivates any existing active assignment.")
    @ApiResponse(responseCode = "201", description = "Workout plan assigned successfully")
    public ResponseEntity<WorkoutAssignmentResponse> assignPlanToTrainee(
            @Parameter(description = "Workout plan ID") @PathVariable UUID id,
            @Valid @RequestBody AssignWorkoutPlanRequest request,
            @AuthenticationPrincipal User currentUser) {
        WorkoutAssignmentResponse response = workoutPlanService.assignPlanToTrainee(id, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/assignments/{traineeId}")
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Unassign workout plan from trainee", description = "Remove a trainee's assignment to a workout plan")
    @ApiResponse(responseCode = "204", description = "Assignment removed successfully")
    public ResponseEntity<Void> unassignPlanFromTrainee(
            @Parameter(description = "Workout plan ID") @PathVariable UUID id,
            @Parameter(description = "Trainee ID") @PathVariable UUID traineeId,
            @AuthenticationPrincipal User currentUser) {
        workoutPlanService.unassignPlanFromTrainee(id, traineeId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/assignments")
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Get plan assignments", description = "Get all assignments for a workout plan")
    public ResponseEntity<List<WorkoutAssignmentResponse>> getPlanAssignments(
            @Parameter(description = "Workout plan ID") @PathVariable UUID id) {
        List<WorkoutAssignmentResponse> response = workoutPlanService.getPlanAssignments(id);
        return ResponseEntity.ok(response);
    }
}
