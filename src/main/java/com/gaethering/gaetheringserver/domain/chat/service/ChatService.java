package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;

public interface ChatService {

    void makeChatRoom(String email, MakeChatRoomRequest makeChatRoomRequest);

    void deleteChatRoom(String email, String chatRoomKey);

}
