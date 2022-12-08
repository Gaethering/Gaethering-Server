package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.domain.MemberProfile;
import com.gaethering.gaetheringserver.member.dto.LoginInfoResponse;
import com.gaethering.gaetheringserver.member.dto.LoginRequest;
import com.gaethering.gaetheringserver.member.dto.LoginResponse;
import com.gaethering.gaetheringserver.member.dto.LogoutRequest;
import com.gaethering.gaetheringserver.member.dto.ReissueTokenRequest;
import com.gaethering.gaetheringserver.member.dto.ReissueTokenResponse;
import com.gaethering.gaetheringserver.member.dto.SignUpRequest;
import com.gaethering.gaetheringserver.member.dto.SignUpResponse;
import com.gaethering.gaetheringserver.member.exception.DuplicatedEmailException;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.exception.auth.TokenIncorrectException;
import com.gaethering.gaetheringserver.member.exception.auth.TokenInvalidException;
import com.gaethering.gaetheringserver.member.exception.auth.TokenNotExistException;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.member.type.Gender;
import com.gaethering.gaetheringserver.member.type.MemberRole;
import com.gaethering.gaetheringserver.member.type.MemberStatus;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.EmailSender;
import com.gaethering.gaetheringserver.util.ImageUploader;
import com.gaethering.gaetheringserver.util.JwtProvider;
import com.gaethering.gaetheringserver.util.RedisUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final ImageUploader imageUploader;
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
    public SignUpResponse signUp(MultipartFile file, SignUpRequest signUpRequest) {

        String imageUrl = imageUploader.uploadImage(file);

        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new DuplicatedEmailException();
        }

        Member newMember = Member.builder()
            .email(signUpRequest.getEmail())
            .nickname(signUpRequest.getNickname())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .name(signUpRequest.getName())
            .birth(LocalDate.parse(signUpRequest.getBirth()))
            .role(MemberRole.ROLE_USER)
            .status(MemberStatus.ACTIVE)
            .isEmailAuth(signUpRequest.isEmailAuth())
            .memberProfile(MemberProfile.builder()
                .phoneNumber(signUpRequest.getPhone())
                .gender(Gender.valueOf(signUpRequest.getGender()))
                .build())
            .pets(new ArrayList<>())
            .build();

        Pet newPet = Pet.builder()
            .name(signUpRequest.getPetName())
            .birth(LocalDate.parse(signUpRequest.getPetBirth()))
            .gender(Gender.valueOf(signUpRequest.getGender()))
            .breed(signUpRequest.getBreed())
            .weight(signUpRequest.getWeight())
            .isNeutered(signUpRequest.isNeutered())
            .description(signUpRequest.getDescription())
            .imageUrl(imageUrl)
            .isRepresentative(true)
            .build();

        newMember.addPet(newPet);

        petRepository.save(newPet);
        memberRepository.save(newMember);

        return SignUpResponse.builder()
            .petName(newPet.getName())
            .imageUrl(newPet.getImageUrl())
            .build();
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

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        String accessToken = request.getAccessToken();

        if (!jwtProvider.validateToken(accessToken)) {
            throw new TokenInvalidException(MemberErrorCode.INVALID_ACCESS_TOKEN);
        }

        Authentication authentication = jwtProvider.getAuthentication(request.getAccessToken());
        String email = authentication.getName();

        redisUtil.deleteData(email);

        Long expiration = jwtProvider.getExpiration(accessToken);
        redisUtil.setBlackList(accessToken, "accessToken", expiration);
    }

    @Override
    public LoginInfoResponse getLoginInfo(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);

        return LoginInfoResponse.of(member);
    }
}
