package com.gymmybro.domain.exercise;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * External ID from ExerciseDB API
     */
    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(nullable = false)
    private String name;

    @Column(name = "target_muscle")
    private String targetMuscle;

    @Column(name = "body_part")
    private String bodyPart;

    /**
     * STRENGTH, CARDIO, STRETCHING
     */
    @Column(nullable = false)
    private String category;

    private String equipment;

    /**
     * GIF URL from ExerciseDB
     */
    @Column(name = "gif_url")
    private String gifUrl;

    /**
     * Additional data stored as JSONB:
     * - instructions: List of instruction strings
     * - secondaryMuscles: List of secondary muscle names
     */
    @Type(JsonType.class)
    @Column(name = "extra_data", columnDefinition = "jsonb")
    private Map<String, Object> extraData;
}
