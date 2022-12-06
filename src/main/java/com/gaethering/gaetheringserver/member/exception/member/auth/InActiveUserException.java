package com.gaethering.gaetheringserver.member.exception.member.auth;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class InActiveUserException extends MemberAuthException {

    public InActiveUserException() {
        super(MemberErrorCode.CANNOT_LOGIN_INACTIVE_USER);
    }
}
