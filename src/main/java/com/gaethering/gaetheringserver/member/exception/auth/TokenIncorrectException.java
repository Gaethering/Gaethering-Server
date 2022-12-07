package com.gaethering.gaetheringserver.member.exception.auth;


import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class TokenIncorrectException extends MemberAuthException {

    public TokenIncorrectException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
