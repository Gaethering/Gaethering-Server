package com.gaethering.gaetheringserver.member.service.auth;

import com.gaethering.gaetheringserver.member.dto.auth.LoginRequest;
import com.gaethering.gaetheringserver.member.dto.auth.LoginResponse;
import com.gaethering.gaetheringserver.member.dto.auth.LogoutRequest;
import com.gaethering.gaetheringserver.member.dto.auth.ReissueTokenRequest;
import com.gaethering.gaetheringserver.member.dto.auth.ReissueTokenResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    ReissueTokenResponse reissue(ReissueTokenRequest request);

    void logout(LogoutRequest request);
}
