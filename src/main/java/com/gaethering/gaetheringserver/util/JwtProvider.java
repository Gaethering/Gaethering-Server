package com.gaethering.gaetheringserver.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

}