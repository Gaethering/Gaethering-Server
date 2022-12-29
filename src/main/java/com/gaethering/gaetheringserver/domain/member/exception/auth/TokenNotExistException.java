package com.gaethering.gaetheringserver.domain.member.exception.auth;

import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class TokenNotExistException extends MemberAuthException {

    public TokenNotExistException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
