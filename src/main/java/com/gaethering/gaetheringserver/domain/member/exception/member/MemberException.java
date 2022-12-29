package com.gaethering.gaetheringserver.domain.member.exception.member;

import com.gaethering.gaetheringserver.core.exception.BusinessException;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends BusinessException {

    private final MemberErrorCode errorCode;

    protected MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
        this.errorCode = memberErrorCode;
    }
}