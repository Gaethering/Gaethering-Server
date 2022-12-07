package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.dto.SignUpRequest;
import com.gaethering.gaetheringserver.member.dto.SignUpResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void sendEmailAuthCode(String email);

    void confirmEmailAuthCode(String code);

    SignUpResponse signUp(MultipartFile file, SignUpRequest signUpRequest);

    boolean modifyNickname(String email, String nickname);
}
