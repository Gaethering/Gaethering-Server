package com.gaethering.gaetheringserver.domain.member.exception.member;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class MemberNotFoundException extends MemberException {

    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}
