package com.gaethering.gaetheringserver.domain.member.dto.profile;

import com.gaethering.gaetheringserver.domain.pet.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProfilePetResponse {

    private Long id;
    private String name;
    private boolean isRepresentative;
    private String imageUrl;

    public static ProfilePetResponse of(Pet pet) {
        return ProfilePetResponse.builder()
            .id(pet.getId())
            .name(pet.getName())
            .isRepresentative(pet.isRepresentative())
            .imageUrl(pet.getImageUrl())
            .build();
    }
}