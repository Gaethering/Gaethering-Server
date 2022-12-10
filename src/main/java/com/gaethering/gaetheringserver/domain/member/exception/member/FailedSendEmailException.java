package com.gaethering.gaetheringserver.domain.member.exception.member;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class FailedSendEmailException extends MemberException {

    public FailedSendEmailException() {
        super(MemberErrorCode.FAILED_SEND_EMAIL);
    }
}
