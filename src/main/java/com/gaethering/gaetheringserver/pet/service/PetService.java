package com.gaethering.gaetheringserver.pet.service;

import com.gaethering.gaetheringserver.pet.dto.PetImageUpdateResponse;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.dto.PetProfileUpdateRequest;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterRequest;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PetService {

    PetProfileResponse getPetProfile(Long id);

    PetImageUpdateResponse updatePetImage(Long id, MultipartFile multipartFile);

    boolean setRepresentativePet(String email, Long petId);

    PetProfileResponse updatePetProfile(Long id, PetProfileUpdateRequest request);

    boolean deletePetProfile(String email, Long id);

    PetRegisterResponse registerPet(String email, MultipartFile file,
        PetRegisterRequest petRegisterRequest);
}
