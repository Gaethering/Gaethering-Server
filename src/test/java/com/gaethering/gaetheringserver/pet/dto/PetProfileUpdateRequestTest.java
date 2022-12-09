package com.gaethering.gaetheringserver.pet.dto;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PetProfileUpdateRequestTest {

	private static Validator validator;

	@BeforeAll
	public static void init() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("반려동물 프로필 수정시 몸무게를 음수값으로 입력한 경우")
	void positivePetWeight_validation() {
		// given
		PetProfileUpdateRequest request = PetProfileUpdateRequest.builder()
			.weight(-1.1f)
			.isNeutered(true)
			.description("test")
			.build();

		// when
		String message = validator.validate(request).stream().findFirst().get().getMessage();

		// then
		assertEquals("반려동물 몸무게는 양수여야 합니다.", message);
	}

	@Test
	@DisplayName("반려동물 프로필 수정시 상세설명을 입력하지 않은 경우")
	void blankPetDescription_validation() {
		// given
		PetProfileUpdateRequest request = PetProfileUpdateRequest.builder()
			.weight(3.5f)
			.isNeutered(true)
			.build();

		// when
		String message = validator.validate(request).stream().findFirst().get().getMessage();

		// then
		assertEquals("반려동물 상세설명은 필수 입력 사항입니다.", message);
	}
}