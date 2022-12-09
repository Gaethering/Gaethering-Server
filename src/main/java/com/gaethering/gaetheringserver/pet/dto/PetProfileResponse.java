package com.gaethering.gaetheringserver.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gaethering.gaetheringserver.member.type.Gender;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PetProfileResponse {

	private String name;

	private LocalDate birth;

	private Gender gender;

	private String breed;

	private float weight;

	@JsonProperty("isNeutered")
	private boolean neutered;

	private String description;

	private String imageUrl;

	public static PetProfileResponse fromEntity(Pet pet) {
		return PetProfileResponse.builder()
			.name(pet.getName())
			.birth(pet.getBirth())
			.gender(pet.getGender())
			.breed(pet.getBreed())
			.weight(pet.getWeight())
			.neutered(pet.isNeutered())
			.description(pet.getDescription())
			.imageUrl(pet.getImageUrl())
			.build();
	}
}
