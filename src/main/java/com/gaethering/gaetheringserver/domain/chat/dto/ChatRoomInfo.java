package com.gaethering.gaetheringserver.domain.chat.dto;

import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatroomMember;
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
public class ChatRoomInfo {

    private String name;
    private String roomKey;
    private String description;
    private Integer maxParticipant;
    private List<WalkingTimeInfo> walkingTimeInfos;
    private List<ChatRoomMemberInfo> chatRoomMemberInfos;

    public static ChatRoomInfo of(ChatRoom chatRoom) {
        List<WalkingTimeInfo> walkingTimeInfos = chatRoom.getWalkingTimes().stream().map(WalkingTimeInfo::of)
            .collect(Collectors.toList());
        List<ChatRoomMemberInfo> chatRoomMemberInfos = chatRoom.getChatroomMembers().stream()
            .map(ChatroomMember::getMember).collect(Collectors.toList()).stream().map(ChatRoomMemberInfo::of)
            .collect(Collectors.toList());
        return ChatRoomInfo.builder()
            .name(chatRoom.getName())
            .roomKey(chatRoom.getRoomKey())
            .description(chatRoom.getDescription())
            .maxParticipant(chatRoom.getMaxParticipantCount())
            .walkingTimeInfos(walkingTimeInfos)
            .chatRoomMemberInfos(chatRoomMemberInfos).build();
    }
}
