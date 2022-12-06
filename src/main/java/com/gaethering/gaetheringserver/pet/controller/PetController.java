package com.gaethering.gaetheringserver.pet.controller;

import com.gaethering.gaetheringserver.pet.dto.RegisterPetRequest;
import com.gaethering.gaetheringserver.pet.dto.RegisterPetResponse;
import com.gaethering.gaetheringserver.pet.service.PetService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api-prefix}/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping("/register")
    public ResponseEntity<RegisterPetResponse> registerPet(
        @RequestPart("image") MultipartFile multipartFile,
        @RequestPart @Valid RegisterPetRequest registerPetRequest) {

        String petName = petService.registerPet(multipartFile, registerPetRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(RegisterPetResponse.builder()
            .petName(petName)
            .build());
    }

}
