package com.gaethering.gaetheringserver.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PetImageUpdateResponse {

    private String imageUrl;
}
