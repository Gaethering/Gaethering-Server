package com.gaethering.gaetheringserver.domain.member.service.member;

import com.gaethering.gaetheringserver.domain.member.dto.auth.LoginInfoResponse;
import com.gaethering.gaetheringserver.domain.member.dto.mypage.MyPostsResponse;
import com.gaethering.gaetheringserver.domain.member.dto.signup.SignUpRequest;
import com.gaethering.gaetheringserver.domain.member.dto.signup.SignUpResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void sendEmailAuthCode(String email);

    void confirmEmailAuthCode(String code);

    SignUpResponse signUp(MultipartFile file, SignUpRequest signUpRequest);

    boolean modifyNickname(String email, String nickname);

    LoginInfoResponse getLoginInfo(String email);

    MyPostsResponse getMyPosts(String email);
}
