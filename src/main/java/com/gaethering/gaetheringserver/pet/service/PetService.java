package com.gaethering.gaetheringserver.pet.service;

import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PetService {

    void updatePetImage(Long id, MultipartFile multipartFile);

    PetProfileResponse getPetProfile(Long id);

}
