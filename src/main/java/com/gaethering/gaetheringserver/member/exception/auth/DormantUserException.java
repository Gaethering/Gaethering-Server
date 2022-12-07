package com.gaethering.gaetheringserver.member.exception.auth;


import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;

public class DormantUserException extends MemberAuthException {

    public DormantUserException() {
        super(MemberErrorCode.CANNOT_LOGIN_DORMANT_USER);
    }
}
