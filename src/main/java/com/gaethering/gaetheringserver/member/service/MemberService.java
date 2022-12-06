package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.dto.*;

public interface MemberService {

    void sendEmailAuthCode(String email);

    void confirmEmailAuthCode(String code);

    String signUp(SignUpRequest signUpRequest);

    LoginResponse login(LoginRequest request);

    ReissueTokenResponse reissue(ReissueTokenRequest request);
}
