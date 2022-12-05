package com.gaethering.gaetheringserver.member.dto;

import com.gaethering.gaetheringserver.pet.domain.Pet;
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

    public static ProfilePetResponse of(Pet pet) {
        return ProfilePetResponse.builder()
            .id(pet.getId())
            .name(pet.getName())
            .isRepresentative(pet.isRepresentative())
            .build();
    }
}
