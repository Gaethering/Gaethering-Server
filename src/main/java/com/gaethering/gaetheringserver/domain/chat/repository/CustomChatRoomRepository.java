package com.gaethering.gaetheringserver.domain.chat.repository;


import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import java.util.List;

public interface CustomChatRoomRepository {

    List<ChatRoom> findChatRoomsByMemberId(Long memberId);
}
