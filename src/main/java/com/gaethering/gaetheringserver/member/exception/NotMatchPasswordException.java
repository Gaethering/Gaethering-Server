package com.gaethering.gaetheringserver.member.exception;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class NotMatchPasswordException extends MemberException {

    public NotMatchPasswordException() {
        super(MemberErrorCode.NOT_MATCH_PASSWORD);
    }
}