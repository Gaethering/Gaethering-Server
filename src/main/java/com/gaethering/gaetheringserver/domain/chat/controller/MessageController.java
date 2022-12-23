package com.gaethering.gaetheringserver.domain.chat.controller;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.service.ChatMessageService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("chat.enter.{roomKey}")
    public ChatMessageResponse enter(@Valid ChatMessageRequest request, @DestinationVariable String roomKey) {
        return chatMessageService.enter(request, roomKey);
    }

    @MessageMapping("chat.send.{roomKey}")
    public ChatMessageResponse send(@Valid ChatMessageRequest request, @DestinationVariable String roomKey) {
        return chatMessageService.send(request, roomKey);
    }
}
