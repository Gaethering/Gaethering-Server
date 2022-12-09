package com.gaethering.gaetheringserver.member.dto.follow;

import com.gaethering.gaetheringserver.member.domain.Member;
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
