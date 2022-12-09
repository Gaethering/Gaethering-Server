package com.gaethering.gaetheringserver.member.exception.member;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.exception.member.MemberException;

public class FailedSendEmailException extends MemberException {

    public FailedSendEmailException() {
        super(MemberErrorCode.FAILED_SEND_EMAIL);
    }
}
