package com.gaethering.gaetheringserver.domain.chat.exception;

import com.gaethering.gaetheringserver.domain.chat.exception.errorcode.ChatErrorCode;

public class ChatRoomOverCrowdException extends ChatException {

    public ChatRoomOverCrowdException() {
        super(ChatErrorCode.OVER_CROWD);
    }
}
