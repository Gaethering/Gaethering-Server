package com.gaethering.gaetheringserver.member.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignUpRequestTest {

    private static Validator validator;

    @BeforeAll
    public static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("회원 가입 시 이메일 형식이 잘못된 경우")
    void invalidEmail_ThrownError_EmailPatternMushMatchPattern() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaetheringgmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("김진호")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("이메일 형식이 잘못되었습니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 이메일 입력하지 않은 경우")
    void emptyEmail_ThrownError_EmailMustNotBlank() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("김진호")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("이메일은 필수 입력 사항입니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 닉네임 길이가 2글자이상 10글자 이하가 아닌 경우")
    void wrongSizeNickname_ThrownError_NicknameMustAdhereSize() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("아아아아아아아아아아아")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("김진호")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("닉네임은 2글자 이상 10글자 이하입니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 비밀번호 입력하지 않은 경우")
    void emptyPassword_ThrownError_PasswordMustNotBlank() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("")
            .passwordCheck("")
            .name("김진호")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("비밀번호는 필수 입력 사항입니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 비밀번호가 일치하지 않는 경우")
    void notMatchPassword_ThrownError_PasswordMustMatch() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!!")
            .name("김진호")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("비밀번호가 일치하지 않습니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 이름 입력하지 않은 경우")
    void emptyName_ThrownError_NameMustNotBlank() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name(" ")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("이름은 필수 입력 사항입니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 전화번호를 입력하지 않은 경우")
    void emptyPhoneNumber_ThrownError_PhoneNumberMustNotBlank() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("asdf")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("전화번호는 필수 입력 사항입니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 입력한 전화번호가 패턴과 일치하지 않는 경우")
    void invalidPhoneNumber_ThrownError_PhoneNumberMustMatchPattern() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("asdf")
            .phone("01012341234")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("전화번호 형식이 잘못되었습니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 생일 형식이 맞지 않는 경우")
    void invalidBirthPattern_ThrownError_BirthMustMatchPattern() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("개더링")
            .phone("010-3230-2498")
            .birth("20211224")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("8자리의 yyyy-MM-dd 형식이어야 합니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 반려동물 이름을 입력하지 않은 경우")
    void emptyPetName_ThrownError_PetNameMustNotBlank() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("김진호")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("반려동물 이름은 필수 입력 사항입니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 펫 생일 형식이 맞지 않는 경우")
    void invalidPetBirthPattern_ThrownError_PetBirthMustMatchPattern() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("개더링")
            .phone("010-3230-2498")
            .birth("2021-12-24")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("20220315")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("8자리의 yyyy-MM-dd 형식이어야 합니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 반려동물 몸무게가 음수인 경우")
    void invalidPetWeight_ThrownError_PetWeightMustBePositive() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("개더링")
            .phone("010-3230-2498")
            .birth("2021-12-24")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(-5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("반려동물 몸무게는 양수이어야 합니다.", message);
    }

    @Test
    @DisplayName("회원 가입 시 성별이 MALE 또는 FEMALE이 아닌 경우")
    void invalidGender_ThrownError_PetWeightMustBeMaleOrFemale() {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("개더링")
            .phone("010-3230-2498")
            .birth("2021-12-24")
            .gender("MALEE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        //when
        String message = validator.validate(request).stream().findFirst().get().getMessage();

        //then
        assertEquals("성별은 MALE, FEMALE 둘 중 하나여야 합니다.", message);
    }

}