package com.gaethering.gaetheringserver.member.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {
    MEMBER_NOT_FOUND("E001", "존재하지 않는 회원입니다."),
    INVALID_EMAIL_AUTH_CODE("E901", "이메일 인증 코드가 유효하지 않습니다."),
    DUPLICATED_EMAIL("E902", "중복된 이메일입니다."),
    FAILED_SEND_EMAIL("E903", "이메일 전송에 실패하였습니다."),
    NOT_MATCH_PASSWORD("E904", "비밀번호가 서로 일치하지 않습니다."),
    INVALID_ARGUMENT("E905", "유효하지 않은 요청 형식입니다.");

    private final String code;
    private final String message;

}
