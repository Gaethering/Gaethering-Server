package com.gaethering.gaetheringserver.domain.member.jwt;

import com.gaethering.gaetheringserver.domain.member.dto.auth.LoginResponse;
import com.gaethering.gaetheringserver.domain.member.dto.auth.ReissueTokenResponse;
import com.gaethering.gaetheringserver.domain.member.exception.auth.TokenNotExistUserInfoException;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.domain.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final RedisService redisService;
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

        redisService.setDataExpire(email, refreshToken, refreshTokenValid);
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
            throw new TokenNotExistUserInfoException(
                MemberErrorCode.CANNOT_FIND_USER_EMAIL_IN_TOKEN);
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
            return headerAuth.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            if (redisService.hasKeyBlackList(token)) {
                return false;
            }

            return !claims.getBody().getExpiration().before(new Date());

        } catch (SignatureException e) {
            log.info("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parser().setSigningKey(key)
            .parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}