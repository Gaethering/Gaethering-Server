package com.gaethering.gaetheringserver.domain.member.exception.auth;


import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;

public class DormantUserException extends MemberAuthException {

    public DormantUserException() {
        super(MemberErrorCode.CANNOT_LOGIN_DORMANT_USER);
    }
}
