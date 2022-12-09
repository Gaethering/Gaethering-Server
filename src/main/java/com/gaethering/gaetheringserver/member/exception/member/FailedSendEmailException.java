package com.gaethering.gaetheringserver.member.exception.member;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class FailedSendEmailException extends MemberException {

    public FailedSendEmailException() {
        super(MemberErrorCode.FAILED_SEND_EMAIL);
    }
}
