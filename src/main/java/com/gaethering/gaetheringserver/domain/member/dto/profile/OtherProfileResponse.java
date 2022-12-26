package com.gaethering.gaetheringserver.domain.member.dto.profile;

import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OtherProfileResponse {

    private String email;
    private String nickname;
    private Gender gender;
    private float mannerDegree;
    private Long followerCount;
    private Long followingCount;
    private int petCount;
    private List<ProfilePetResponse> pets;

    public static OtherProfileResponse of(Member member, Long followerCount, Long followingCount) {
        List<ProfilePetResponse> pets = member.getPets().stream().map(ProfilePetResponse::of)
            .collect(Collectors.toList());
        return OtherProfileResponse.builder()
            .email(member.getEmail())
            .nickname(member.getNickname())
            .gender(member.getMemberProfile().getGender())
            .mannerDegree(member.getMemberProfile().getMannerDegree())
            .followerCount(followerCount)
            .followingCount(followingCount)
            .petCount(member.getPets().size())
            .pets(pets)
            .build();
    }
}
