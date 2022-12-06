package com.gaethering.gaetheringserver.member.exception.member.auth;

import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class TokenInvalidException extends MemberAuthException {

    public TokenInvalidException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
