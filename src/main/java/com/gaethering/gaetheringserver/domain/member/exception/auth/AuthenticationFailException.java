package com.gaethering.gaetheringserver.domain.member.exception.auth;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class AuthenticationFailException extends MemberAuthException {

    public AuthenticationFailException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
