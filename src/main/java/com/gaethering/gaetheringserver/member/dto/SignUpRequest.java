package com.gaethering.gaetheringserver.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gaethering.gaetheringserver.member.type.Gender;
import java.time.LocalDate;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @Email(message = "이메일 형식이 잘못되었습니다.")
    private String email;
    @NotEmpty
    private String nickname;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordCheck;
    @NotEmpty
    private String name;
    @NotEmpty
    private String phone;
    private LocalDate birth;
    private Gender gender;
    @JsonProperty("isEmailAuth")
    private boolean isEmailAuth;

    @AssertTrue
    boolean isMatchPassword() {
        return this.password.equals(this.passwordCheck);
    }

}
