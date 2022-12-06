package com.gaethering.gaetheringserver.member.exception.member.auth;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberAuthException extends RuntimeException {
    private final MemberErrorCode errorCode;

    public MemberAuthException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.getMessage());
        this.errorCode = memberErrorCode;
    }
}
