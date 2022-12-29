package com.gaethering.gaetheringserver.domain.member.exception.member;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class DuplicatedEmailException extends MemberException {

    public DuplicatedEmailException() {
        super(MemberErrorCode.DUPLICATED_EMAIL);
    }
}
