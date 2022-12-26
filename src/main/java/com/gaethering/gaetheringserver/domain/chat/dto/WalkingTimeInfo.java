package com.gaethering.gaetheringserver.domain.chat.dto;

import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalkingTimeInfo {

    private String dayOfWeek;
    private String time;

    public static WalkingTime toEntity(WalkingTimeInfo walkingTimeInfo) {
        return WalkingTime.builder()
            .dayOfWeek(walkingTimeInfo.getDayOfWeek())
            .time(walkingTimeInfo.getTime())
            .build();
    }

    public static WalkingTimeInfo of(WalkingTime walkingTime) {
        return WalkingTimeInfo.builder()
            .dayOfWeek(walkingTime.getDayOfWeek())
            .time(walkingTime.getTime()).build();
    }
}
