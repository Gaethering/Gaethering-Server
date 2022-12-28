package com.gaethering.gaetheringserver.domain.chat.repository.impl;

import static com.gaethering.gaetheringserver.domain.chat.entity.QChatRoom.chatRoom;
import static com.gaethering.gaetheringserver.domain.chat.entity.QChatroomMember.chatroomMember;

import com.gaethering.gaetheringserver.core.repository.support.Querydsl4RepositorySupport;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.repository.CustomChatRoomRepository;
import java.util.List;

public class ChatRoomRepositoryImpl extends Querydsl4RepositorySupport implements
    CustomChatRoomRepository {

    public ChatRoomRepositoryImpl() {
        super(ChatRoom.class);
    }

    @Override
    public List<ChatRoom> findChatRoomsByMemberId(Long memberId) {
        return selectFrom(chatRoom)
            .join(chatRoom.chatroomMembers, chatroomMember).fetchJoin()
            .where(chatroomMember.member.id.eq(memberId))
            .fetch();
    }
}
