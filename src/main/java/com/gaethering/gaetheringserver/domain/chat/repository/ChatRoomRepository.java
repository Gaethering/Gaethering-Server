package com.gaethering.gaetheringserver.domain.chat.repository;

import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomKey(String chatRoomKey);

}
