package com.gaethering.gaetheringserver.domain.chat.exception.errorcode;

import com.gaethering.gaetheringserver.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_ROOM_NOT_FOUND("E110", "해당 채팅방은 존재하지 않습니다."),
    OVER_CROWD("E111", "채팅방 인원을 초과하였습니다.");

    private final String code;
    private final String message;

}
