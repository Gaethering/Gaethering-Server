package com.gaethering.gaetheringserver.util;

import com.gaethering.gaetheringserver.member.dto.LoginResponse;
import com.gaethering.gaetheringserver.member.dto.ReissueTokenResponse;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.exception.member.auth.TokenNotExistUserInfoException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final RedisUtil redisUtil;
    private final UserDetailsService userDetailsService;

    @Value("${spring.jwt.secret}")
    private String key;

    @Value("${spring.jwt.valid.accessToken}")
    private Long accessTokenValid;

    @Value("${spring.jwt.valid.refreshToken}")
    private Long refreshTokenValid;


    @PostConstruct
    protected void init() {
        key = Base64.getEncoder().encodeToString(key.getBytes()); // Base64 인코딩
    }

    public String createAccessToken(String email, Long tokenValid) {
        Claims claims = Jwts.claims().setSubject(email); // JWT payload 에 저장되는 정보단위
        Date date = new Date();
        return Jwts.builder()
                .setClaims(claims)  // 정보 저장
                .setIssuedAt(date) // 토큰 발행 시간 정보
                .setExpiration(new Date(date.getTime() + tokenValid)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String createRefreshToken(Long tokenValid) {
        Date date = new Date();
        return Jwts.builder()
                .setIssuedAt(date) // 토큰 발행 시간 정보
                .setExpiration(new Date(date.getTime() + tokenValid)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public LoginResponse createTokensByLogin(Authentication authentication) {

        String email = authentication.getName();

        String accessToken = createAccessToken(email, accessTokenValid);
        String refreshToken = createRefreshToken(refreshTokenValid);

        redisUtil.setDataExpire(email, refreshToken, refreshTokenValid);
        return new LoginResponse(accessToken, refreshToken);
    }

    public ReissueTokenResponse reissueAccessToken(String email) {

        String accessToken = createAccessToken(email, accessTokenValid);

        return new ReissueTokenResponse(accessToken);
    }

    public Authentication getAuthentication(String accessToken) {
        String email = getUserEmail(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            throw new TokenNotExistUserInfoException(MemberErrorCode.CANNOT_FIND_USER_EMAIL_IN_TOKEN);
        }
    }

}

