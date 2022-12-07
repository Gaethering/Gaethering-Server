package com.gaethering.gaetheringserver.member.exception.auth;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class AuthorizationFailException extends MemberAuthException {

    public AuthorizationFailException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
