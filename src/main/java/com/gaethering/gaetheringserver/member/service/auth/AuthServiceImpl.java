package com.gaethering.gaetheringserver.member.service.auth;

import com.gaethering.gaetheringserver.member.dto.auth.LoginRequest;
import com.gaethering.gaetheringserver.member.dto.auth.LoginResponse;
import com.gaethering.gaetheringserver.member.dto.auth.LogoutRequest;
import com.gaethering.gaetheringserver.member.dto.auth.ReissueTokenRequest;
import com.gaethering.gaetheringserver.member.dto.auth.ReissueTokenResponse;
import com.gaethering.gaetheringserver.member.exception.auth.TokenIncorrectException;
import com.gaethering.gaetheringserver.member.exception.auth.TokenInvalidException;
import com.gaethering.gaetheringserver.member.exception.auth.TokenNotExistException;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.util.jwt.JwtProvider;
import com.gaethering.gaetheringserver.util.redis.RedisUtil;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


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

}