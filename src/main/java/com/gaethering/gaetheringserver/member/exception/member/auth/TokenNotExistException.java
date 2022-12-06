package com.gaethering.gaetheringserver.member.exception.member.auth;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class TokenNotExistException extends MemberAuthException {

    public TokenNotExistException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
