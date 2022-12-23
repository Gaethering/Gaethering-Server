package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.WalkingTimeInfo;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.WalkingTimeRepository;
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
    private final WalkingTimeRepository walkingTimeRepository;

    @Override
    @Transactional
    public void makeChatRoom(String email, MakeChatRoomRequest makeChatRoomRequest) {
        memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        String roomKey = UUID.randomUUID().toString();

        ChatRoom chatRoom = ChatRoom.builder()
            .roomKey(roomKey)
            .description(makeChatRoomRequest.getDescription())
            .walkingTimes(new ArrayList<>())
            .build();

        List<WalkingTime> walkingTimes = makeChatRoomRequest.getWalkingTimes().stream()
            .map(WalkingTimeInfo::toEntity).collect(Collectors.toList());

        walkingTimes.forEach(chatRoom::addWalkingTime);

        chatRoomRepository.save(chatRoom);
        walkingTimeRepository.saveAll(walkingTimes);
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
        return chatRoom.getChatMessages().stream().sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
            .map(ChatMessageResponse::of).collect(Collectors.toList());
    }
}
