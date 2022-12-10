package com.gaethering.gaetheringserver.domain.member.exception.auth;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class InActiveUserException extends MemberAuthException {

    public InActiveUserException() {
        super(MemberErrorCode.CANNOT_LOGIN_INACTIVE_USER);
    }
}
