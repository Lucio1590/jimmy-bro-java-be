package com.gymmybro.application.dto.response;

import com.gymmybro.domain.workout.WorkoutDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Workout day response with nested blocks and exercises.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDayResponse {

    private UUID id;
    private Integer dayNumber;
    private String name;
    private String description;
    private boolean isRestDay;
    private List<WorkoutBlockResponse> blocks;

    /**
     * Create response from entity.
     */
    public static WorkoutDayResponse fromEntity(WorkoutDay day) {
        WorkoutDayResponseBuilder builder = WorkoutDayResponse.builder()
                .id(day.getId())
                .dayNumber(day.getDayNumber())
                .name(day.getName())
                .description(day.getDescription())
                .isRestDay(day.isRestDay());

        if (day.getWorkoutBlocks() != null) {
            builder.blocks(day.getWorkoutBlocks().stream()
                    .map(WorkoutBlockResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
