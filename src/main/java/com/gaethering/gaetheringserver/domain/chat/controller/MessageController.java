package com.gaethering.gaetheringserver.domain.chat.controller;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest;
import com.gaethering.gaetheringserver.domain.chat.service.ChatMessageService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("chat.send")
    public void send(@Valid ChatMessageRequest request) {
        chatMessageService.send(request);
    }
}
