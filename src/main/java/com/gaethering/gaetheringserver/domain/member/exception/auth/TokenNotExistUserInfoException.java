package com.gaethering.gaetheringserver.domain.member.exception.auth;


import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class TokenNotExistUserInfoException extends MemberAuthException {

    public TokenNotExistUserInfoException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
