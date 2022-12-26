package com.gaethering.gaetheringserver.domain.chat.dto;

import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.pet.entity.Pet;
import com.gaethering.gaetheringserver.domain.pet.exception.RepresentativePetNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMemberInfo {

    private Long id;
    private String nickname;
    private String representPetImageUrl;

    public static ChatRoomMemberInfo of(Member member) {
        Pet pet = member.getPets().stream().filter(Pet::isRepresentative).findFirst()
            .orElseThrow(RepresentativePetNotFoundException::new);
        return ChatRoomMemberInfo.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .representPetImageUrl(pet.getImageUrl())
            .build();
    }
}
