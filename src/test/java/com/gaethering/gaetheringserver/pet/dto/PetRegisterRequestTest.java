package com.gaethering.gaetheringserver.pet.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gaethering.gaetheringserver.domain.pet.dto.PetRegisterRequest;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PetRegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    public static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("반려동물 추가 등록 시 반려동물 이름을 입력하지 않은 경우")
    void emptyPetName_ThrownError_PetNameMustNotBlank() {
        //given
        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("반려동물 이름은 필수 입력 사항입니다.", message);
    }

    @Test
    @DisplayName("반려동물 추가 등록 시 반려동물 생일 형식이 맞지 않는 경우")
    void invalidPetBirthPattern_ThrownError_PetBirthMustMatchPattern() {
        //given
        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("뽀삐")
            .petBirth("202203-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("8자리의 yyyy-MM-dd 형식이어야 합니다.", message);
    }

    @Test
    @DisplayName("반려동물 추가 등록 시 반려동물 몸무게가 음수인 경우")
    void invalidPetWeight_ThrownError_PetWeightMustBePositive() {
        //given
        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(-5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("반려동물 몸무게는 양수여야 합니다.", message);
    }

    @Test
    @DisplayName("반려동물 추가 등록 시 성별이 MALE 또는 FEMALE 이 아닌 경우")
    void invalidPetGender_ThrownError_PetWeightMustBeMaleOrFemale() {
        //given
        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("MIDDLE")
            .description("___")
            .neutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("성별은 MALE, FEMALE 둘 중 하나여야 합니다.", message);
    }

}