package com.gaethering.gaetheringserver.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gaethering.gaetheringserver.core.validator.EnumValid;
import com.gaethering.gaetheringserver.core.validator.LocalDateValid;
import com.gaethering.gaetheringserver.core.type.Gender;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetRegisterRequest {

    @NotBlank(message = "반려동물 이름은 필수 입력 사항입니다.")
    private String petName;
    @LocalDateValid(message = "8자리의 yyyy-MM-dd 형식이어야 합니다.", pattern = "yyyy-MM-dd")
    private String petBirth;
    @Positive(message = "반려동물 몸무게는 양수여야 합니다.")
    private float weight;
    private String breed;
    @EnumValid(enumClass = Gender.class, message = "성별은 MALE, FEMALE 둘 중 하나여야 합니다.")
    private String petGender;
    @NotEmpty
    private String description;
    @JsonProperty("isNeutered")
    private boolean isNeutered;

}
