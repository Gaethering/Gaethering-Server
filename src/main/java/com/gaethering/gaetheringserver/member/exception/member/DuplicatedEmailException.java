package com.gaethering.gaetheringserver.member.exception.member;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.exception.member.MemberException;

public class DuplicatedEmailException extends MemberException {

    public DuplicatedEmailException() {
        super(MemberErrorCode.DUPLICATED_EMAIL);
    }
}
