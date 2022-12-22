package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest;

public interface ChatMessageService {

    boolean send(ChatMessageRequest chatMessageRequest);
}
