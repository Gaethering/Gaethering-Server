package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.dto.LoginInfoResponse;
import com.gaethering.gaetheringserver.member.dto.LoginRequest;
import com.gaethering.gaetheringserver.member.dto.LoginResponse;
import com.gaethering.gaetheringserver.member.dto.LogoutRequest;
import com.gaethering.gaetheringserver.member.dto.ReissueTokenRequest;
import com.gaethering.gaetheringserver.member.dto.ReissueTokenResponse;
import com.gaethering.gaetheringserver.member.dto.SignUpRequest;
import com.gaethering.gaetheringserver.member.dto.SignUpResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void sendEmailAuthCode(String email);

    void confirmEmailAuthCode(String code);

    SignUpResponse signUp(MultipartFile file, SignUpRequest signUpRequest);

    boolean modifyNickname(String email, String nickname);

    LoginResponse login(LoginRequest request);

    ReissueTokenResponse reissue(ReissueTokenRequest request);

    void logout(LogoutRequest request);

    LoginInfoResponse getLoginInfo(String email);

}
