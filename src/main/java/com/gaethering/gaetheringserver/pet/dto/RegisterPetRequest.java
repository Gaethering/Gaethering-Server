package com.gaethering.gaetheringserver.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gaethering.gaetheringserver.member.type.Gender;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPetRequest {

    private String petName;

    private LocalDate petBirth;

    private float weight;

    private String breed;

    private Gender gender;

    private String description;

    @JsonProperty("isNeutered")
    private boolean isNeutered;

}
