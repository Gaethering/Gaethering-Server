package com.gaethering.gaetheringserver.domain.member.exception.auth;


import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class TokenInvalidException extends MemberAuthException {

    public TokenInvalidException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
