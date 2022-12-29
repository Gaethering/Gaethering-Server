package com.gaethering.gaetheringserver.domain.member.dto.follow;

import com.gaethering.gaetheringserver.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FollowResponse {

    private Long id;
    private String name;
    private String nickname;

    public static FollowResponse of(Member member) {
        return FollowResponse.builder()
            .id(member.getId())
            .name(member.getName())
            .nickname(member.getNickname()).build();
    }
}
