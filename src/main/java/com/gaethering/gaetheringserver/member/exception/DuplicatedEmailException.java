package com.gaethering.gaetheringserver.member.exception;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class DuplicatedEmailException extends MemberException {

    public DuplicatedEmailException() {
        super(MemberErrorCode.DUPLICATED_EMAIL);
    }
}
