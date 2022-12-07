package com.gaethering.gaetheringserver.member.exception.auth;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class AuthenticationFailException extends MemberAuthException {

    public AuthenticationFailException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
