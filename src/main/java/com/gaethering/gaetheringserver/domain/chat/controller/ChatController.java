package com.gaethering.gaetheringserver.domain.chat.controller;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomResponse;
import com.gaethering.gaetheringserver.domain.chat.service.ChatService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<MakeChatRoomResponse> makeChatRoom(Principal principal,
        @RequestBody @Valid MakeChatRoomRequest makeChatRoomRequest) {
        MakeChatRoomResponse response = chatService.makeChatRoom(principal.getName(), makeChatRoomRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/chat/room/{roomKey}")
    public ResponseEntity<Void> deleteChatRoom(Principal principal,
        @PathVariable(value = "roomKey") String roomKey) {
        chatService.deleteChatRoom(principal.getName(), roomKey);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat/room/{roomKey}")
    public ResponseEntity<ChatRoomInfo> getChatRoomInfo(@PathVariable String roomKey) {
        ChatRoomInfo chaRoomInformation = chatService.getChaRoomInformation(roomKey);
        return ResponseEntity.ok(chaRoomInformation);
    }

    @GetMapping("/chat/room/{roomKey}/history")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(@PathVariable String roomKey) {
        List<ChatMessageResponse> chatHistory = chatService.getChatHistory(roomKey);
        return ResponseEntity.ok(chatHistory);
    }
}
