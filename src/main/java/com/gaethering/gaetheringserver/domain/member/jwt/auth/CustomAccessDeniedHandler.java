package com.gaethering.gaetheringserver.domain.member.jwt.auth;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
        HttpServletResponse response, AccessDeniedException accessDeniedException)
        throws IOException {

        log.info("UnAuthorized!!! " + accessDeniedException.getMessage());
        response.sendRedirect("/exception/accessDenied");

    }
}