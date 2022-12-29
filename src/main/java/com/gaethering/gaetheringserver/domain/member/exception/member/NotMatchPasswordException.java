package com.gaethering.gaetheringserver.domain.member.exception.member;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class NotMatchPasswordException extends MemberException {

    public NotMatchPasswordException() {
        super(MemberErrorCode.NOT_MATCH_PASSWORD);
    }
}
