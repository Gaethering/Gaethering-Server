package com.gaethering.gaetheringserver.domain.chat.dto;

import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
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

    private String description;
    private List<WalkingTime> walkingTimes;
}
