package com.gaethering.gaetheringserver.domain.chat.exception;

import com.gaethering.gaetheringserver.domain.chat.exception.errorcode.ChatErrorCode;

public class ChatRoomNotFoundException extends ChatException {

    public ChatRoomNotFoundException() {
        super(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
    }
}
