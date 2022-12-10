package com.gaethering.gaetheringserver.domain.member.exception.auth;


import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class TokenIncorrectException extends MemberAuthException {

    public TokenIncorrectException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
