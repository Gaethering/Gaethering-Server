package com.gaethering.gaetheringserver.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetProfileUpdateRequest {

	@Positive(message = "반려동물 몸무게는 양수여야 합니다.")
	private float weight;

	@JsonProperty("isNeutered")
	private boolean neutered;

	@NotBlank(message = "반려동물 상세설명은 필수 입력 사항입니다.")
	private String description;

}
