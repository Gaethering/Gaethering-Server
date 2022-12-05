package com.gaethering.gaetheringserver.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.dto.SignUpRequest;
import com.gaethering.gaetheringserver.member.exception.DuplicatedEmailException;
import com.gaethering.gaetheringserver.member.exception.NotMatchPasswordException;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.repository.member.MemberProfileRepository;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.member.type.Gender;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("회원 가입 성공")
    void signUp_Success() {
        //given
        SignUpRequest request = getSignUpRequest();

        given(memberRepository.existsByEmail(anyString()))
            .willReturn(false);

        given(passwordEncoder.encode(anyString()))
            .willReturn(anyString());

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        //when
        String nickname = memberService.signUp(request);

        //then
        assertEquals(request.getNickname(), nickname);
        verify(memberRepository, times(1)).save(captor.capture());
    }

    @Test
    @DisplayName("회원가입 실패_중복된 이메일이 있을 경우")
    void signUp_ExceptionThrown_DuplicatedEmail() {
        //given
        SignUpRequest request = getSignUpRequest();

        given(memberRepository.existsByEmail(anyString()))
            .willReturn(true);

        //when
        DuplicatedEmailException exception = assertThrows(
            DuplicatedEmailException.class, () -> memberService.signUp(request));

        //then
        assertEquals(MemberErrorCode.DUPLICATED_EMAIL.getCode(),
            exception.getErrorCode().getCode());
    }

    @Test
    @DisplayName("회원가입 실패_비밀번호가 일치하지 않는 경우")
    void signUp_ExceptionThrown_NotMatchPassword() {
        //given
        SignUpRequest request = getNotMatchSignUpRequest();

        given(memberRepository.existsByEmail(anyString()))
            .willReturn(false);

        //when
        NotMatchPasswordException exception = assertThrows(
            NotMatchPasswordException.class, () -> memberService.signUp(request));

        //then
        assertEquals(MemberErrorCode.NOT_MATCH_PASSWORD.getCode(),
            exception.getErrorCode().getCode());
    }

    private static SignUpRequest getSignUpRequest() {

        return SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("김진호")
            .phone("010-3230-2498")
            .birth(LocalDate.of(2022, 02, 15))
            .gender(Gender.MALE)
            .isEmailAuth(true)
            .build();
    }

    private static SignUpRequest getNotMatchSignUpRequest() {

        return SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer")
            .name("김진호")
            .phone("010-3230-2498")
            .birth(LocalDate.of(2022, 02, 15))
            .gender(Gender.MALE)
            .isEmailAuth(true)
            .build();
    }

}