package com.gaethering.gaetheringserver.member.exception.member;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class InvalidEmailAuthCodeException extends MemberException {

    public InvalidEmailAuthCodeException() {
        super(MemberErrorCode.INVALID_EMAIL_AUTH_CODE);
    }
}
