package com.gaethering.gaetheringserver.member.exception;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class MemberNotFoundException extends MemberException {

    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}
