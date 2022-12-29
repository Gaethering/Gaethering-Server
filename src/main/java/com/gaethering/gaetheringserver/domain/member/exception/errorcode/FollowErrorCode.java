package com.gaethering.gaetheringserver.domain.member.exception.errorcode;

import com.gaethering.gaetheringserver.core.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements ErrorCode {
    FOLLOW_NOT_FOUND("E101", "해당 하는 팔로우 관계가 존재하지 않습니다.");

    private final String code;
    private final String message;
}
