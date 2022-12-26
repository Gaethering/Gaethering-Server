package com.gaethering.gaetheringserver.domain.chat.exception;

import com.gaethering.gaetheringserver.core.exception.BusinessException;
import com.gaethering.gaetheringserver.domain.chat.exception.errorcode.ChatErrorCode;
import lombok.Getter;

@Getter
public class ChatException extends BusinessException {

    private final ChatErrorCode errorCode;

    protected ChatException(ChatErrorCode chatErrorCode) {
        super(chatErrorCode);
        this.errorCode = chatErrorCode;
    }
}
