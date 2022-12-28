package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomListInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomListResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.WalkingTimeInfo;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatMessageRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.WalkingTimeRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final WalkingTimeRepository walkingTimeRepository;

    @Override
    @Transactional
    public MakeChatRoomResponse makeChatRoom(String email,
        MakeChatRoomRequest makeChatRoomRequest) {
        memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String roomKey = UUID.randomUUID().toString();
        ChatRoom chatRoom = makeChatRoom(makeChatRoomRequest, roomKey);
        List<WalkingTime> walkingTimes = makeChatRoomRequest.getWalkingTimes().stream()
            .map(WalkingTimeInfo::toEntity).collect(Collectors.toList());
        walkingTimes.forEach(chatRoom::addWalkingTime);
        chatRoomRepository.save(chatRoom);
        walkingTimeRepository.saveAll(walkingTimes);
        return MakeChatRoomResponse.builder().roomKey(roomKey).build();
    }

    private static ChatRoom makeChatRoom(MakeChatRoomRequest makeChatRoomRequest, String roomKey) {
        return ChatRoom.builder()
            .roomKey(roomKey)
            .name(makeChatRoomRequest.getName())
            .maxParticipantCount(makeChatRoomRequest.getMaxParticipantCount())
            .description(makeChatRoomRequest.getDescription())
            .walkingTimes(new ArrayList<>())
            .build();
    }

    @Override
    public ChatRoomInfo getChaRoomInformation(String roomKey) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomKey(roomKey)
            .orElseThrow(ChatRoomNotFoundException::new);
        return ChatRoomInfo.of(chatRoom);
    }

    @Override
    public List<ChatMessageResponse> getChatHistory(String roomKey) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomKey(roomKey)
            .orElseThrow(ChatRoomNotFoundException::new);
        return chatRoom.getChatMessages().stream()
            .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
            .map(ChatMessageResponse::of).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChatRoom(String email, String chatRoomKey) {
        memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        ChatRoom chatRoom = chatRoomRepository.findByRoomKey(chatRoomKey)
            .orElseThrow(ChatRoomNotFoundException::new);

        chatMessageRepository.deleteAllByChatRoom(chatRoom);
        walkingTimeRepository.deleteAllByChatRoom(chatRoom);
        chatRoomRepository.delete(chatRoom);
    }

    @Override
    public ChatRoomListResponse getLocalChatRooms(String email) {
        memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        List<ChatRoomListInfo> chatRoomInfos = chatRoomRepository.findAll().stream()
            .map(ChatRoomListInfo::of).collect(Collectors.toList());

        return ChatRoomListResponse.builder()
            .numberOfChatRooms(chatRoomInfos.size())
            .chatRooms(chatRoomInfos)
            .build();
    }

    @Override
    public ChatRoomListResponse getMyChatRooms(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);

        List<ChatRoomListInfo> chatRoomInfos = chatRoomRepository.findChatRoomsByMemberId(
                member.getId())
            .stream().map(ChatRoomListInfo::of).collect(Collectors.toList());

        return ChatRoomListResponse.builder()
            .numberOfChatRooms(chatRoomInfos.size())
            .chatRooms(chatRoomInfos)
            .build();
    }
}
