package com.gaethering.gaetheringserver.domain.pet.controller;

import com.gaethering.gaetheringserver.domain.pet.dto.PetImageUpdateResponse;
import com.gaethering.gaetheringserver.domain.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.domain.pet.dto.PetProfileUpdateRequest;
import com.gaethering.gaetheringserver.domain.pet.dto.PetRegisterRequest;
import com.gaethering.gaetheringserver.domain.pet.dto.PetRegisterResponse;
import com.gaethering.gaetheringserver.domain.pet.service.PetService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PatchMapping("/mypage/pets/{petId}/representative")
    public ResponseEntity<Void> setRepresentativePet(@PathVariable Long petId,
        Principal principal) {
        petService.setRepresentativePet(principal.getName(), petId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mypage/pets/{petId}/image")
    public ResponseEntity<PetImageUpdateResponse> updatePetImage(@PathVariable("petId") Long id,
        @RequestPart("image") MultipartFile multipartFile) {
        return ResponseEntity.ok(petService.updatePetImage(id, multipartFile));
    }

    @GetMapping("/pets/{petId}/profile")
    public ResponseEntity<PetProfileResponse> getPetProfile(@PathVariable("petId") Long id) {
        return ResponseEntity.ok(petService.getPetProfile(id));
    }

    @PatchMapping("/mypage/pets/{petId}")
    public ResponseEntity<PetProfileResponse> updatePetProfile(@PathVariable("petId") Long id,
        @RequestBody @Valid PetProfileUpdateRequest request) {

        return ResponseEntity.ok(petService.updatePetProfile(id, request));
    }

    @DeleteMapping("/mypage/pets/{petId}")
    public ResponseEntity<Void> deletePetProfile(@PathVariable("petId") Long id,
        Principal principal) {
        petService.deletePetProfile(principal.getName(), id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/pets/register")
    public ResponseEntity<PetRegisterResponse> registerPet(
        @RequestPart("image") MultipartFile multipartFile,
        @RequestPart("data") @Valid PetRegisterRequest petRegisterRequest,
        Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(petService.registerPet(principal.getName(), multipartFile, petRegisterRequest));
    }
}
