package com.gaethering.gaetheringserver.domain.chat.controller;

import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.service.ChatService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat/room")
    public ResponseEntity<Void> makeChatRoom(Principal principal,
        @RequestBody @Valid MakeChatRoomRequest makeChatRoomRequest) {
        chatService.makeChatRoom(principal.getName(), makeChatRoomRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/chat/room/{chatRoomKey}")
    public ResponseEntity<Void> deleteChatRoom(Principal principal,
        @PathVariable(value = "chatRoomKey") String chatRoomKey) {
        chatService.deleteChatRoom(principal.getName(), chatRoomKey);
        return ResponseEntity.ok().build();
    }
}
