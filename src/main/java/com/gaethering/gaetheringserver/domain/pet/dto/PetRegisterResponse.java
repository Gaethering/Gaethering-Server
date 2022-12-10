package com.gaethering.gaetheringserver.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PetRegisterResponse {

    private String petName;

    private String imageUrl;

}
