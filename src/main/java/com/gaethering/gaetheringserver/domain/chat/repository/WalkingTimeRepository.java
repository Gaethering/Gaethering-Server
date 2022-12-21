package com.gaethering.gaetheringserver.domain.chat.repository;

import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalkingTimeRepository extends JpaRepository<WalkingTime, Long> {

    @Modifying
    @Query("delete from WalkingTime w where w.chatRoom = :chatRoom")
    void deleteAllByChatRoomId(@Param("chatRoom") ChatRoom chatRoom);

}
