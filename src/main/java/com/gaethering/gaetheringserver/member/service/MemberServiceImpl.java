package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.domain.MemberProfile;
import com.gaethering.gaetheringserver.member.dto.*;
import com.gaethering.gaetheringserver.member.exception.DuplicatedEmailException;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.exception.NotMatchPasswordException;
import com.gaethering.gaetheringserver.member.exception.auth.TokenIncorrectException;
import com.gaethering.gaetheringserver.member.exception.auth.TokenInvalidException;
import com.gaethering.gaetheringserver.member.exception.auth.TokenNotExistException;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.member.type.MemberRole;
import com.gaethering.gaetheringserver.member.type.MemberStatus;
import com.gaethering.gaetheringserver.util.EmailSender;

import java.util.Objects;
import java.util.UUID;

import com.gaethering.gaetheringserver.util.JwtProvider;
import com.gaethering.gaetheringserver.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    @Transactional
    public void sendEmailAuthCode(String email) {

        if (memberRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException();
        }

        String authCode = UUID.randomUUID().toString();

        emailSender.sendAuthMail(email, authCode);
    }

    @Override
    @Transactional
    public void confirmEmailAuthCode(String code) {

        emailSender.confirmAuthCode(code);
    }

    @Override
    @Transactional
    public String signUp(SignUpRequest signUpRequest) {

        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new DuplicatedEmailException();
        }

        if (!signUpRequest.getPassword().equals(signUpRequest.getPasswordCheck())) {
            throw new NotMatchPasswordException();
        }

        Member newMember = Member.builder()
            .email(signUpRequest.getEmail())
            .nickname(signUpRequest.getNickname())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .role(MemberRole.ROLE_USER)
            .status(MemberStatus.ACTIVE)
            .isEmailAuth(signUpRequest.isEmailAuth())
            .memberProfile(MemberProfile.builder()
                .phoneNumber(signUpRequest.getPhone())
                .gender(signUpRequest.getGender())
                .build())
            .build();

        memberRepository.save(newMember);

        return newMember.getNickname();
    }

    @Override
    public boolean modifyNickname(String email, String nickname) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);
        member.setNickname(nickname);
        return true;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication
                = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtProvider.createTokensByLogin(authentication);
    }

    @Override
    @Transactional
    public ReissueTokenResponse reissue(ReissueTokenRequest request) {

        if (!jwtProvider.validateToken(request.getRefreshToken())) {
            throw new TokenInvalidException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = jwtProvider.getAuthentication(request.getAccessToken());
        String email = authentication.getName();

        String refreshToken = redisUtil.getData(email);

        if (Objects.isNull(refreshToken)) {
            throw new TokenNotExistException(MemberErrorCode.NOT_EXIST_REFRESH_TOKEN);
        }

        if (!request.getRefreshToken().equals(refreshToken)) {
            throw new TokenIncorrectException(MemberErrorCode.INCORRECT_REFRESH_TOKEN);
        }
        return jwtProvider.reissueAccessToken(email);
    }
}
