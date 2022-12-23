package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatMessage;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatroomMember;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomOverCrowdException;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatMessageRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomMemberRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final RabbitTemplate messagingTemplate;

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";

    @Override
    @Transactional
    public ChatMessageResponse enter(ChatMessageRequest chatMessageRequest, String roomKey) {
        Member member = memberRepository.findById(chatMessageRequest.getMemberId())
            .orElseThrow(MemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findByRoomKey(roomKey)
            .orElseThrow(ChatRoomNotFoundException::new);
        Optional<ChatroomMember> optionalChatroomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom,
            member);
        ChatMessageResponse response = ChatMessageResponse.makeResponseFromRequest(chatMessageRequest);
        response.setContent(member.getNickname() + "님이 입장하였습니다.");
        checkMaxParticipantCountAndAddRoomMember(member, chatRoom, optionalChatroomMember);
        return saveAndSendChatMessage(chatMessageRequest, roomKey, member, chatRoom, response);
    }

    @Override
    @Transactional
    public ChatMessageResponse send(ChatMessageRequest chatMessageRequest, String roomKey) {
        Member member = memberRepository.findById(chatMessageRequest.getMemberId())
            .orElseThrow(MemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findByRoomKey(roomKey)
            .orElseThrow(ChatRoomNotFoundException::new);
        ChatMessageResponse response = ChatMessageResponse.makeResponseFromRequest(chatMessageRequest);
        return saveAndSendChatMessage(chatMessageRequest, roomKey, member, chatRoom, response);
    }

    private ChatMessageResponse saveAndSendChatMessage(ChatMessageRequest chatMessageRequest, String roomKey,
        Member member, ChatRoom chatRoom, ChatMessageResponse response) {
        saveChatMessage(chatMessageRequest, member, chatRoom);
        messagingTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + roomKey, response);
        return response;
    }

    private void checkMaxParticipantCountAndAddRoomMember(Member member, ChatRoom chatRoom, Optional<ChatroomMember> optionalChatroomMember) {
        if (optionalChatroomMember.isEmpty()) {
            if (chatRoom.getChatroomMembers().size() >= chatRoom.getMaxParticipantCount()) {
                throw new ChatRoomOverCrowdException();
            }
            chatRoom.addChatroomMember(ChatroomMember.builder().member(member).chatRoom(chatRoom).build());
        }
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
