package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.dto.SignUpRequest;

public interface MemberService {

    void sendEmailAuthCode(String email);

    void confirmEmailAuthCode(String code);

    String signUp(SignUpRequest signUpRequest);

    boolean modifyNickname(String email, String nickname);
}
