package com.gaethering.gaetheringserver.pet.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetProfileUpdateRequest {

	@NotNull(message = "반려동물 몸무게는 필수 입력 사항입니다.")
	private float weight;

	@NotNull(message = "중성화 여부는 필수 입력 사항입니다.")
	private boolean isNeutered;

	@NotBlank(message = "반려동물 상세설명은 필수 입력 사항입니다.")
	private String description;

}
