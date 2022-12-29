package com.gaethering.gaetheringserver.domain.member.exception.member;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class InvalidEmailAuthCodeException extends MemberException {

    public InvalidEmailAuthCodeException() {
        super(MemberErrorCode.INVALID_EMAIL_AUTH_CODE);
    }
}
