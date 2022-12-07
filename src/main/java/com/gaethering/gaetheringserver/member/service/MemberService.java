package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.dto.*;

public interface MemberService {

    void sendEmailAuthCode(String email);

    void confirmEmailAuthCode(String code);

    String signUp(SignUpRequest signUpRequest);

    boolean modifyNickname(String email, String nickname);

    LoginResponse login(LoginRequest request);

    ReissueTokenResponse reissue(ReissueTokenRequest request);

    void logout(LogoutRequest request);
}
