package com.gaethering.gaetheringserver.member.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowErrorCode {
    FOLLOW_NOT_FOUND("E002", "해당 하는 팔로우 관계가 존재하지 않습니다.");

    private final String code;
    private final String message;
}
