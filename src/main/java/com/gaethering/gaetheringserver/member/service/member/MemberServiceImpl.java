package com.gaethering.gaetheringserver.member.service.member;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.domain.MemberProfile;
import com.gaethering.gaetheringserver.member.dto.auth.LoginInfoResponse;
import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.member.dto.signup.SignUpRequest;
import com.gaethering.gaetheringserver.member.dto.signup.SignUpResponse;
import com.gaethering.gaetheringserver.member.exception.member.DuplicatedEmailException;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.member.type.MemberRole;
import com.gaethering.gaetheringserver.member.type.MemberStatus;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.email.EmailSender;
import com.gaethering.gaetheringserver.util.upload.ImageUploader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public LoginInfoResponse getLoginInfo(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);

        return LoginInfoResponse.of(member);
    }
}
