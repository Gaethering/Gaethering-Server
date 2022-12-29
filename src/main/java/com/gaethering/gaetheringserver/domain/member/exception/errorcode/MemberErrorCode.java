package com.gaethering.gaetheringserver.domain.member.exception.errorcode;

import com.gaethering.gaetheringserver.core.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND("E001", "존재하지 않는 회원입니다."),
    INVALID_EMAIL_AUTH_CODE("E002", "이메일 인증 코드가 유효하지 않습니다."),
    DUPLICATED_EMAIL("E003", "중복된 이메일입니다."),
    FAILED_SEND_EMAIL("E004", "이메일 전송에 실패하였습니다."),
    NOT_MATCH_PASSWORD("E005", "비밀번호가 서로 일치하지 않습니다."),
    INVALID_ARGUMENT("E006", "유효하지 않은 요청 형식입니다."),
    CANNOT_LOGIN_INACTIVE_USER("E007", "비활성화된 계정입니다."),
    CANNOT_LOGIN_DORMANT_USER("E008", "휴면 계정입니다."),
    INVALID_REFRESH_TOKEN("E009",
        "refresh token이 유효하지 않아 access token 재발급이 불가능합니다."),
    INCORRECT_REFRESH_TOKEN("E010",
        "refresh token이 일치하지 않아 access token 재발급이 불가능합니다."),
    NOT_EXIST_REFRESH_TOKEN("E011",
        "refresh token이 존재하지 않아 access token 재발급이 불가능합니다."),
    INVALID_ACCESS_TOKEN("E012", "access token이 유효하지 않습니다."),
    FAIL_TO_AUTHENTICATION("E013", "사용자 인증에 실패하였습니다."),
    CANNOT_FIND_USER_EMAIL_IN_TOKEN("E014", "사용자 정보를 찾을 수 없습니다."),
    FAIL_TO_AUTHORIZATION("E015", "사용자 권한이 없습니다.");

    private final String code;
    private final String message;

}
