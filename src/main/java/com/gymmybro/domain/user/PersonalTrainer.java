package com.gymmybro.domain.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PersonalTrainer extends User {

    @OneToMany(mappedBy = "personalTrainer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Trainee> trainees = new ArrayList<>();

    // We override getAuthorities to ensure role is correct?
    // The base class uses 'role' field which is mapped to the column.
    // When saving, 'role' field is ignored. But the DiscriminatorValue "PT" is
    // saved.
    // When loading, 'role' field is populated from the column.
    // So getAuthorities in base class should work for loaded entities.
}
