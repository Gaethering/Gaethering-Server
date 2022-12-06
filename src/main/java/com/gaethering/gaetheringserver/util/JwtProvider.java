package com.gaethering.gaetheringserver.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;

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

}

