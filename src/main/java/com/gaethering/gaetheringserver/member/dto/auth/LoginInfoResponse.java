package com.gaethering.gaetheringserver.member.dto.auth;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.exception.RepresentativePetNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginInfoResponse {

    private String petName;
    private String imageUrl;
    private String nickname;

    public static LoginInfoResponse of(Member member) {
        Pet pet = member.getPets().stream().filter(Pet::isRepresentative).findFirst()
            .orElseThrow(RepresentativePetNotFoundException::new);

        return LoginInfoResponse.builder()
            .nickname(member.getNickname())
            .petName(pet.getName())
            .imageUrl(pet.getImageUrl())
            .build();
    }

}
