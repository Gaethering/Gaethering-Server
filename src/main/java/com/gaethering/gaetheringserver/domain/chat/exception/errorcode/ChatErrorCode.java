package com.gaethering.gaetheringserver.domain.chat.exception.errorcode;

import com.gaethering.gaetheringserver.core.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_ROOM_NOT_FOUND("E701", "존재하지 않는 채팅방입니다.");

    private final String code;
    private final String message;
}
