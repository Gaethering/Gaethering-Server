package com.gaethering.gaetheringserver.pet.controller;

import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterRequest;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterResponse;
import com.gaethering.gaetheringserver.pet.service.PetService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> updatePetImage(@PathVariable("petId") Long id,
        @RequestPart("image") MultipartFile multipartFile) {
        return ResponseEntity.ok(petService.updatePetImage(id, multipartFile));
    }

    @GetMapping("/pets/{petId}/profile")
    public ResponseEntity<PetProfileResponse> getPetProfile(@PathVariable("petId") Long id) {
        return ResponseEntity.ok(petService.getPetProfile(id));
    }

    @PostMapping("/pets/register")
    public ResponseEntity<PetRegisterResponse> registerPet(
        @RequestPart("image") MultipartFile multipartFile,
        @RequestPart("data") @Valid PetRegisterRequest petRegisterRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(petService.registerPet(multipartFile, petRegisterRequest));
    }

}
