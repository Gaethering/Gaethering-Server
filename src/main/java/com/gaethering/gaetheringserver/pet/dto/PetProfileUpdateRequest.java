package com.gaethering.gaetheringserver.pet.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PetProfileUpdateRequest {

	@NotBlank(message = "반려동물 몸무게는 필수 입력 사항입니다.")
	private float weight;

	@NotBlank(message = "중성화 여부는 필수 입력 사항입니다.")
	private boolean isNeutered;

	@NotBlank(message = "반려동물 상세설명은 필수 입력 사항입니다.")
	private String description;

}
