package com.gaethering.gaetheringserver.pet.service;

import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PetService {

	PetProfileResponse getPetProfile(Long id);

	String updatePetImage(Long id, MultipartFile multipartFile);

    boolean setRepresentativePet(String email, Long petId);
}
