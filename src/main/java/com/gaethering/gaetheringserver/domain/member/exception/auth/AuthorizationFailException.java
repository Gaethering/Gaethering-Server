package com.gaethering.gaetheringserver.domain.member.exception.auth;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class AuthorizationFailException extends MemberAuthException {

    public AuthorizationFailException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
