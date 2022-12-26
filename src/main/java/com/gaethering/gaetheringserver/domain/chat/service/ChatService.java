package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import java.util.List;

public interface ChatService {

    void makeChatRoom(String email, MakeChatRoomRequest makeChatRoomRequest);

    ChatRoomInfo getChaRoomInformation(String roomKey);

    List<ChatMessageResponse> getChatHistory(String roomKey);

    void deleteChatRoom(String email, String chatRoomKey);
}
