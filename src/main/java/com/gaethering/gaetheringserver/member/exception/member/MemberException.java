package com.gaethering.gaetheringserver.member.exception.member;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {

    private final MemberErrorCode errorCode;

    protected MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.getMessage());
        this.errorCode = memberErrorCode;
    }
}