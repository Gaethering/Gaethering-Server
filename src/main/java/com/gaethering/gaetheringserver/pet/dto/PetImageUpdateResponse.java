package com.gaethering.gaetheringserver.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PetImageUpdateResponse {

	private String imageUrl;
}
