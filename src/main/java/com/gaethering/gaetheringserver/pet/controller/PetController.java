package com.gaethering.gaetheringserver.pet.controller;

import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.service.PetServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	private final PetServiceImpl petService;

	@PatchMapping("/mypage/pets/{petId}/image")
	public ResponseEntity<String> updatePetImage(@PathVariable("petId") Long id,
		@RequestPart("file") MultipartFile multipartFile) {

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/pets/{petId}/profile")
	public ResponseEntity<PetProfileResponse> getPetProfile(@PathVariable("petId") Long id) {

		return ResponseEntity.ok(petService.getPetProfile(id));
	}

}
