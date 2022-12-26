package com.gaethering.gaetheringserver.domain.chat.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MakeChatRoomRequest {

    private String name;
    private int maxParticipantCount;
    private String description;
    private List<WalkingTimeInfo> walkingTimes;
}
