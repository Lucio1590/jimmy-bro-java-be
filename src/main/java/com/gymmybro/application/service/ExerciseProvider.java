package com.gymmybro.application.service;

import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import java.util.List;

/**
 * Interface for exercise data provider operations.
 * Decouples the application from specific exercise APIs (like ExerciseDB).
 */
public interface ExerciseProvider {

    List<ExerciseDbApiResponse> fetchAllExercises(int limit, int offset);

    List<ExerciseDbApiResponse> searchByName(String name);

    List<ExerciseDbApiResponse> filterByBodyPart(String bodyPart);

    List<ExerciseDbApiResponse> filterByTarget(String target);

    List<ExerciseDbApiResponse> filterByEquipment(String equipment);

    List<String> getBodyParts();

    List<String> getTargets();

    List<String> getEquipment();

    ExerciseDbApiResponse getExerciseById(String externalId);
}
