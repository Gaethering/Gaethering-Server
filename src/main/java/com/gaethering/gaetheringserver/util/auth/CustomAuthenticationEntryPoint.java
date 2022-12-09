package com.gaethering.gaetheringserver.util.auth;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException authException)
        throws IOException {

        log.info("UnAuthorized!!! " + authException.getMessage());
        response.sendRedirect("/exception/authenticationFailed");

    }
}