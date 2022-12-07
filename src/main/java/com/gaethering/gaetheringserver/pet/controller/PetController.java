package com.gaethering.gaetheringserver.pet.controller;

import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class PetController {

	private final PetService petService;

	@PatchMapping("/mypage/pets/{petId}/image")
	public ResponseEntity<String> updatePetImage(@PathVariable("petId") Long id,
		@RequestPart("image") MultipartFile multipartFile) {

		return ResponseEntity.ok(petService.updatePetImage(id, multipartFile));
	}

	@GetMapping("/pets/{petId}/profile")
	public ResponseEntity<PetProfileResponse> getPetProfile(@PathVariable("petId") Long id) {

		return ResponseEntity.ok(petService.getPetProfile(id));
	}

}
