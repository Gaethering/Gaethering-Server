package com.gaethering.gaetheringserver.domain.member.jwt.auth;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.core.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.FAIL_TO_AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        log.info("UnAuthorized!!! " + accessDeniedException.getMessage());
        ErrorResponse errorResponse
                = new ErrorResponse(FAIL_TO_AUTHORIZATION.getCode(),
                FAIL_TO_AUTHORIZATION.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(errorResponse));
        writer.flush();

    }
}