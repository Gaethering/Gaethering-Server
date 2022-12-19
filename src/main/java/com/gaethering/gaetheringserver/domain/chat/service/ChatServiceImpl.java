package com.gaethering.gaetheringserver.domain.chat.service;

import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.WalkingTimeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final WalkingTimeRepository walkingTimeRepository;

    @Override
    public void makeChatRoom(String email, MakeChatRoomRequest makeChatRoomRequest) {
        String roomKey = UUID.randomUUID().toString();

        ChatRoom chatRoom = ChatRoom.builder()
            .roomKey(roomKey)
            .description(makeChatRoomRequest.getDescription())
            .walkingTimes(new ArrayList<>())
            .build();

        List<WalkingTime> walkingTimes = makeChatRoomRequest.getWalkingTimes();

        walkingTimes.forEach(chatRoom::addWalkingTime);

        chatRoomRepository.save(chatRoom);
        walkingTimeRepository.saveAll(walkingTimes);
    }
}
