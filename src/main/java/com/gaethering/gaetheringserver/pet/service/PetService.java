package com.gaethering.gaetheringserver.pet.service;

import com.gaethering.gaetheringserver.pet.dto.PetImageUpdateResponse;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PetService {

	PetProfileResponse getPetProfile(Long id);

	PetImageUpdateResponse updatePetImage(Long id, MultipartFile multipartFile);

    boolean setRepresentativePet(String email, Long petId);

	PetProfileResponse updatePetProfile(Long id, float weight, boolean isNeutered, String description);

	boolean deletePetProfile(String email, Long id);
}
