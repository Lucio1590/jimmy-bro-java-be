package com.gymmybro.presentation.controller;

import com.gymmybro.application.dto.request.CompleteWorkoutRequest;
import com.gymmybro.application.dto.request.LogExerciseEntryRequest;
import com.gymmybro.application.dto.request.StartWorkoutRequest;
import com.gymmybro.application.dto.response.*;
import com.gymmybro.application.service.WorkoutExecutionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for workout execution and logging operations.
 */
@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
@Tag(name = "Workout Execution", description = "Workout logging, tracking, and history operations")
@SecurityRequirement(name = "bearerAuth")
public class WorkoutExecutionController {

    private final WorkoutExecutionService workoutExecutionService;

    @GetMapping("/active")
    @PreAuthorize("hasRole('TRAINEE')")
    @Operation(summary = "Get active workout", description = "Get today's workout based on the trainee's current assignment")
    public ResponseEntity<ActiveWorkoutResponse> getActiveWorkout(
            @AuthenticationPrincipal User currentUser) {
        ActiveWorkoutResponse response = workoutExecutionService.getActiveWorkout(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logs")
    @PreAuthorize("hasRole('TRAINEE')")
    @Operation(summary = "Start workout session", description = "Start a new workout session for a specific workout day")
    @ApiResponse(responseCode = "201", description = "Workout session started")
    public ResponseEntity<WorkoutLogResponse> startWorkout(
            @Valid @RequestBody StartWorkoutRequest request,
            @AuthenticationPrincipal User currentUser) {
        WorkoutLogResponse response = workoutExecutionService.startWorkout(
                currentUser.getId(), request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logs/{logId}/entries")
    @PreAuthorize("hasRole('TRAINEE')")
    @Operation(summary = "Log exercise entry", description = "Log a set/entry for an exercise within an ongoing workout")
    @ApiResponse(responseCode = "201", description = "Exercise entry logged")
    public ResponseEntity<WorkoutLogEntryResponse> logExerciseEntry(
            @Parameter(description = "Workout log ID") @PathVariable UUID logId,
            @Valid @RequestBody LogExerciseEntryRequest request) {
        WorkoutLogEntryResponse response = workoutExecutionService.logExerciseEntry(logId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/logs/{logId}/complete")
    @PreAuthorize("hasRole('TRAINEE')")
    @Operation(summary = "Complete workout", description = "Mark an ongoing workout as completed")
    public ResponseEntity<WorkoutLogResponse> completeWorkout(
            @Parameter(description = "Workout log ID") @PathVariable UUID logId,
            @Valid @RequestBody CompleteWorkoutRequest request) {
        WorkoutLogResponse response = workoutExecutionService.completeWorkout(logId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs/{logId}")
    @PreAuthorize("hasAnyRole('TRAINEE', 'PT', 'ADMIN')")
    @Operation(summary = "Get workout log", description = "Get details of a specific workout log")
    public ResponseEntity<WorkoutLogResponse> getWorkoutLog(
            @Parameter(description = "Workout log ID") @PathVariable UUID logId) {
        WorkoutLogResponse response = workoutExecutionService.getWorkoutLog(logId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('TRAINEE')")
    @Operation(summary = "Get workout history", description = "Get paginated list of completed workouts for the current trainee")
    public ResponseEntity<PaginatedResponse<WorkoutLogResponse>> getWorkoutHistory(
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<WorkoutLogResponse> response = workoutExecutionService.getWorkoutHistory(currentUser.getId(),
                pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{traineeId}")
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Get trainee workout history", description = "Get paginated list of completed workouts for a specific trainee (PT/Admin only)")
    public ResponseEntity<PaginatedResponse<WorkoutLogResponse>> getTraineeWorkoutHistory(
            @Parameter(description = "Trainee ID") @PathVariable UUID traineeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<WorkoutLogResponse> response = workoutExecutionService.getWorkoutHistory(traineeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('TRAINEE')")
    @Operation(summary = "Get workout statistics", description = "Get workout statistics for the current trainee")
    public ResponseEntity<WorkoutStatsResponse> getWorkoutStats(
            @AuthenticationPrincipal User currentUser) {
        WorkoutStatsResponse response = workoutExecutionService.getWorkoutStats(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/{traineeId}")
    @PreAuthorize("hasAnyRole('PT', 'ADMIN')")
    @Operation(summary = "Get trainee workout statistics", description = "Get workout statistics for a specific trainee (PT/Admin only)")
    public ResponseEntity<WorkoutStatsResponse> getTraineeWorkoutStats(
            @Parameter(description = "Trainee ID") @PathVariable UUID traineeId) {
        WorkoutStatsResponse response = workoutExecutionService.getWorkoutStats(traineeId);
        return ResponseEntity.ok(response);
    }
}
