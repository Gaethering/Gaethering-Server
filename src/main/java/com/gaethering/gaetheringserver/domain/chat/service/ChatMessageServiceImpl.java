package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatMessage;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatMessageRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomRepository;
import com.gaethering.gaetheringserver.domain.chat.util.DestinationUtil;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public boolean send(ChatMessageRequest chatMessageRequest) {
        Member member = memberRepository.findById(chatMessageRequest.getMemberId())
            .orElseThrow(MemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findByRoomKey(chatMessageRequest.getRoomKey())
            .orElseThrow(ChatRoomNotFoundException::new);
        saveChatMessage(chatMessageRequest, member, chatRoom);
        messagingTemplate.convertAndSend(DestinationUtil.makeChatRoomDestination(chatMessageRequest.getRoomKey()),
            ChatMessageResponse.makeResponseFromRequest(chatMessageRequest));
        return true;
    }

    private void saveChatMessage(ChatMessageRequest chatMessageRequest, Member member, ChatRoom chatRoom) {
        ChatMessage chatMessage = makeChatMessage(chatMessageRequest, member, chatRoom);
        chatRoom.addChatMessage(chatMessage);
        chatMessageRepository.save(chatMessage);
    }

    private static ChatMessage makeChatMessage(ChatMessageRequest chatMessageRequest, Member member, ChatRoom chatRoom) {
        return ChatMessage.builder()
            .member(member)
            .chatRoom(chatRoom)
            .content(chatMessageRequest.getContent())
            .build();
    }
}
