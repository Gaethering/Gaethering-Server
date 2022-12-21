package com.gaethering.gaetheringserver.domain.chat.repository;

import com.gaethering.gaetheringserver.domain.chat.entity.ChatMessage;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Modifying
    @Query("delete from ChatMessage c where c.chatRoom = :chatRoom")
    void deleteAllByChatRoomId(@Param("chatRoom") ChatRoom chatRoom);

}
