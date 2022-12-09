package com.gaethering.gaetheringserver.member.exception.member;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.exception.member.MemberException;

public class NotMatchPasswordException extends MemberException {

    public NotMatchPasswordException() {
        super(MemberErrorCode.NOT_MATCH_PASSWORD);
    }
}
