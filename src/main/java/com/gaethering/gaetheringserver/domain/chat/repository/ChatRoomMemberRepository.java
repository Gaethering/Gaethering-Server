package com.gaethering.gaetheringserver.domain.chat.repository;

import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatroomMember;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatroomMember, Long> {

    Optional<ChatroomMember> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
