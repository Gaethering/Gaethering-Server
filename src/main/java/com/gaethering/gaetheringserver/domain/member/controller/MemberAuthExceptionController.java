package com.gaethering.gaetheringserver.domain.member.controller;

import com.gaethering.gaetheringserver.domain.member.exception.auth.AuthenticationFailException;
import com.gaethering.gaetheringserver.domain.member.exception.auth.AuthorizationFailException;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception")
public class MemberAuthExceptionController {

    @GetMapping("/accessDenied")
    public String accessDenied() {
        throw new AuthorizationFailException(MemberErrorCode.FAIL_TO_AUTHORIZATION);
    }

    @GetMapping("/authenticationFailed")
    public String authenticationFailed() {
        throw new AuthenticationFailException(MemberErrorCode.FAIL_TO_AUTHENTICATION);
    }
}
