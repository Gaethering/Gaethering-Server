package com.gaethering.gaetheringserver.member.exception.auth;

import com.gaethering.gaetheringserver.core.exception.BusinessException;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberAuthException extends BusinessException {
    private final MemberErrorCode errorCode;

    public MemberAuthException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
        this.errorCode = memberErrorCode;
    }
}
