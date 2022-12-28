package com.gaethering.gaetheringserver.domain.chat.dto;

import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListInfo {

    private String name;
    private String roomKey;
    private String description;
    private Integer maxParticipant;
    private List<WalkingTimeInfo> walkingTimeInfos;
    private Integer nowParticipant;

    public static ChatRoomListInfo of(ChatRoom chatRoom) {
        List<WalkingTimeInfo> walkingTimeInfos = chatRoom.getWalkingTimes().stream()
            .map(WalkingTimeInfo::of)
            .collect(Collectors.toList());
        Integer nowParticipant = chatRoom.getChatroomMembers().size();
        return ChatRoomListInfo.builder()
            .name(chatRoom.getName())
            .roomKey(chatRoom.getRoomKey())
            .description(chatRoom.getDescription())
            .maxParticipant(chatRoom.getMaxParticipantCount())
            .walkingTimeInfos(walkingTimeInfos)
            .nowParticipant(nowParticipant).build();
    }
}
