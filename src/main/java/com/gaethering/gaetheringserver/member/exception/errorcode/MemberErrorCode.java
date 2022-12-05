package com.gaethering.gaetheringserver.member.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {
    MEMBER_NOT_FOUND("E001", "존재하지 않는 회원입니다.");

    private final String code;
    private final String message;

}
