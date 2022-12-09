package com.gaethering.gaetheringserver.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gaethering.gaetheringserver.core.validator.EnumValid;
import com.gaethering.gaetheringserver.core.validator.LocalDateValid;
import com.gaethering.gaetheringserver.member.type.Gender;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "이메일 형식이 잘못되었습니다.")
    private String email;
    @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2글자 이상 10글자 이하입니다.")
    private String nickname;
    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$",
    message = "비밀번호는 최소 8자, 최대 15자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자이어야합니다.")
    private String password;
    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    private String passwordCheck;
    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name;
    @NotBlank(message = "전화번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 잘못되었습니다.")
    private String phone;
    @LocalDateValid(message = "8자리의 yyyy-MM-dd 형식이어야 합니다.", pattern = "yyyy-MM-dd")
    private String birth;
    @EnumValid(enumClass = Gender.class, message = "성별은 MALE, FEMALE 둘 중 하나여야 합니다.")
    private String gender;
    @JsonProperty("isEmailAuth")
    private boolean isEmailAuth;
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

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    boolean isMatchPassword() {
        return this.password.equals(this.passwordCheck);
    }

}
