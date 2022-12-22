package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;

public interface ChatService {

    void makeChatRoom(String email, MakeChatRoomRequest makeChatRoomRequest);

    ChatRoomInfo getChaRoomInformation(String roomKey);
}
