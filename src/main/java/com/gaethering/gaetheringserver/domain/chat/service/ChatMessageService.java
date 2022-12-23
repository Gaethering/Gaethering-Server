package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;

public interface ChatMessageService {

    ChatMessageResponse enter(ChatMessageRequest chatMessageRequest, String roomKey);
    ChatMessageResponse send(ChatMessageRequest chatMessageRequest, String roomKey);
}
