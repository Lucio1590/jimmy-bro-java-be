package com.gymmybro.application.service;

import com.gymmybro.application.dto.request.CompleteWorkoutRequest;
import com.gymmybro.application.dto.request.LogExerciseEntryRequest;
import com.gymmybro.application.dto.request.StartWorkoutRequest;
import com.gymmybro.application.dto.response.*;
import com.gymmybro.domain.user.User;
import com.gymmybro.domain.workout.*;
import com.gymmybro.exception.BadRequestException;
import com.gymmybro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for workout execution and logging operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutExecutionService {

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutLogEntryRepository workoutLogEntryRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutPlanAssignmentRepository assignmentRepository;

    /**
     * Get today's active workout for a trainee.
     */
    @Transactional(readOnly = true)
    public ActiveWorkoutResponse getActiveWorkout(UUID traineeId) {
        log.info("Getting active workout for trainee {}", traineeId);
        LocalDate today = LocalDate.now();

        // Find current active assignment
        Optional<WorkoutPlanAssignment> assignment = assignmentRepository.findCurrentAssignment(traineeId, today);

        if (assignment.isEmpty()) {
            return ActiveWorkoutResponse.builder()
                    .hasActiveAssignment(false)
                    .message("No active workout plan assigned")
                    .build();
        }

        WorkoutPlan plan = assignment.get().getWorkoutPlan();
        LocalDate startDate = assignment.get().getStartDate();

        // Calculate which day of the program we're on
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, today);
        int totalDays = plan.getWorkoutDays().size();

        if (totalDays == 0) {
            return ActiveWorkoutResponse.builder()
                    .hasActiveAssignment(true)
                    .workoutPlanId(plan.getId())
                    .workoutPlanName(plan.getName())
                    .message("Workout plan has no days configured")
                    .build();
        }

        // Calculate current day number (cycling through the program)
        int currentDayNumber = ((int) (daysSinceStart % totalDays)) + 1;
        WorkoutDay workoutDay = workoutDayRepository.findByWorkoutPlanIdAndDayNumber(plan.getId(), currentDayNumber);

        if (workoutDay == null) {
            return ActiveWorkoutResponse.builder()
                    .hasActiveAssignment(true)
                    .workoutPlanId(plan.getId())
                    .workoutPlanName(plan.getName())
                    .message("Day " + currentDayNumber + " not found in plan")
                    .build();
        }

        // Check if workout already logged today
        Optional<WorkoutLog> todaysLog = workoutLogRepository.findByTraineeIdAndWorkoutDate(traineeId, today);

        ActiveWorkoutResponse.ActiveWorkoutResponseBuilder builder = ActiveWorkoutResponse.builder()
                .hasActiveAssignment(true)
                .workoutPlanId(plan.getId())
                .workoutPlanName(plan.getName())
                .workoutDayId(workoutDay.getId())
                .workoutDayName(workoutDay.getName())
                .dayNumber(workoutDay.getDayNumber())
                .isRestDay(workoutDay.isRestDay());

        if (workoutDay.isRestDay()) {
            builder.message("Today is a rest day!");
        } else {
            // Add workout blocks with exercises
            builder.blocks(workoutDay.getWorkoutBlocks().stream()
                    .map(WorkoutBlockResponse::fromEntity)
                    .collect(Collectors.toList()));

            if (todaysLog.isPresent()) {
                WorkoutLog log = todaysLog.get();
                builder.currentLogId(log.getId())
                        .workoutInProgress(log.getCompletedAt() == null)
                        .workoutCompletedToday(log.getCompletedAt() != null);
            }
        }

        return builder.build();
    }

    /**
     * Start a new workout session.
     */
    @Transactional
    public WorkoutLogResponse startWorkout(UUID traineeId, StartWorkoutRequest request, User trainee) {
        log.info("Starting workout for trainee {} on day {}", traineeId, request.getWorkoutDayId());

        WorkoutDay workoutDay = workoutDayRepository.findById(request.getWorkoutDayId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout day not found"));

        if (workoutDay.isRestDay()) {
            throw new BadRequestException("Cannot start a workout on a rest day");
        }

        LocalDate workoutDate = request.getWorkoutDate() != null ? request.getWorkoutDate() : LocalDate.now();

        // Check if already logged for this date
        Optional<WorkoutLog> existingLog = workoutLogRepository.findByTraineeIdAndWorkoutDate(traineeId, workoutDate);
        if (existingLog.isPresent()) {
            throw new BadRequestException("Workout already logged for this date");
        }

        WorkoutLog log = WorkoutLog.builder()
                .trainee(trainee)
                .workoutDay(workoutDay)
                .workoutDate(workoutDate)
                .startedAt(Instant.now())
                .build();

        WorkoutLog saved = workoutLogRepository.save(log);
        return WorkoutLogResponse.fromEntity(saved);
    }

    /**
     * Log an exercise entry for an ongoing workout.
     */
    @Transactional
    public WorkoutLogEntryResponse logExerciseEntry(UUID logId, LogExerciseEntryRequest request) {
        log.info("Logging exercise entry for log {} - exercise {}", logId, request.getWorkoutExerciseId());

        WorkoutLog workoutLog = workoutLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout log not found"));

        if (workoutLog.getCompletedAt() != null) {
            throw new BadRequestException("Cannot add entries to a completed workout");
        }

        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(request.getWorkoutExerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found"));

        WorkoutLogEntry entry = WorkoutLogEntry.builder()
                .workoutLog(workoutLog)
                .workoutExercise(workoutExercise)
                .setNumber(request.getSetNumber())
                .actualWeight(request.getWeight())
                .actualReps(request.getRepsPerformed())
                .actualDurationSeconds(request.getDurationSeconds())
                .rpe(request.getRpe())
                .notes(request.getNotes())
                .isCompleted(true)
                .build();

        workoutLog.getEntries().add(entry);
        workoutLogRepository.save(workoutLog);

        return WorkoutLogEntryResponse.fromEntity(entry);
    }

    /**
     * Complete an ongoing workout session.
     */
    @Transactional
    public WorkoutLogResponse completeWorkout(UUID logId, CompleteWorkoutRequest request) {
        log.info("Completing workout {}", logId);

        WorkoutLog workoutLog = workoutLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout log not found"));

        if (workoutLog.getCompletedAt() != null) {
            throw new BadRequestException("Workout already completed");
        }

        workoutLog.setCompletedAt(Instant.now());
        workoutLog.setNotes(request.getNotes());
        workoutLog.setRating(request.getRating());

        // Calculate duration
        if (workoutLog.getStartedAt() != null) {
            long minutes = Duration.between(workoutLog.getStartedAt(), workoutLog.getCompletedAt()).toMinutes();
            workoutLog.setDurationMinutes((int) minutes);
        }

        WorkoutLog saved = workoutLogRepository.save(workoutLog);
        return WorkoutLogResponse.fromEntity(saved);
    }

    /**
     * Get workout history for a trainee.
     */
    @Transactional(readOnly = true)
    public PaginatedResponse<WorkoutLogResponse> getWorkoutHistory(UUID traineeId, Pageable pageable) {
        log.info("Getting workout history for trainee {}", traineeId);

        Page<WorkoutLog> page = workoutLogRepository.findByTraineeIdOrderByWorkoutDateDesc(traineeId, pageable);

        List<WorkoutLogResponse> content = page.getContent().stream()
                .map(WorkoutLogResponse::fromEntity)
                .collect(Collectors.toList());

        return PaginatedResponse.<WorkoutLogResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    /**
     * Get a specific workout log.
     */
    @Transactional(readOnly = true)
    public WorkoutLogResponse getWorkoutLog(UUID logId) {
        WorkoutLog log = workoutLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout log not found"));
        return WorkoutLogResponse.fromEntity(log);
    }

    /**
     * Get workout statistics for a trainee.
     */
    @Transactional(readOnly = true)
    public WorkoutStatsResponse getWorkoutStats(UUID traineeId) {
        log.info("Getting workout stats for trainee {}", traineeId);

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        LocalDate monthAgo = today.minusDays(30);

        WorkoutStatsResponse.WorkoutStatsResponseBuilder builder = WorkoutStatsResponse.builder()
                .totalWorkouts(workoutLogRepository.countByTraineeId(traineeId))
                .workoutsThisWeek(workoutLogRepository.countByTraineeIdAndDateRange(traineeId, weekAgo, today))
                .workoutsThisMonth(workoutLogRepository.countByTraineeIdAndDateRange(traineeId, monthAgo, today))
                .averageDurationMinutes(workoutLogRepository.getAverageDurationByTraineeId(traineeId))
                .averageRating(workoutLogRepository.getAverageRatingByTraineeId(traineeId));

        // Get last workout
        workoutLogRepository.findTopByTraineeIdOrderByWorkoutDateDesc(traineeId)
                .ifPresent(log -> builder.lastWorkoutDate(log.getWorkoutDate()));

        // Get workouts by day of week
        List<Object[]> dayOfWeekStats = workoutLogRepository.getWorkoutCountByDayOfWeek(traineeId);
        Map<Integer, Long> workoutsByDay = new HashMap<>();
        for (Object[] row : dayOfWeekStats) {
            Integer dayOfWeek = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            workoutsByDay.put(dayOfWeek, count);
        }
        builder.workoutsByDayOfWeek(workoutsByDay);

        // Calculate streak (simplified)
        builder.currentStreak(calculateCurrentStreak(traineeId));

        return builder.build();
    }

    /**
     * Calculate current workout streak.
     */
    private Integer calculateCurrentStreak(UUID traineeId) {
        LocalDate today = LocalDate.now();
        List<WorkoutLog> recentLogs = workoutLogRepository.findByTraineeIdAndDateRange(
                traineeId, today.minusDays(365), today);

        if (recentLogs.isEmpty()) {
            return 0;
        }

        Set<LocalDate> workoutDates = recentLogs.stream()
                .map(WorkoutLog::getWorkoutDate)
                .collect(Collectors.toSet());

        int streak = 0;
        LocalDate checkDate = today;

        while (workoutDates.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        return streak;
    }
}
