package com.gaethering.gaetheringserver.pet.controller;

import com.gaethering.gaetheringserver.pet.service.PetService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
