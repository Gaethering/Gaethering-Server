package com.gaethering.gaetheringserver.pet.service;

import org.springframework.web.multipart.MultipartFile;

public interface PetService {

    String updatePetImage(Long id, MultipartFile multipartFile);

}
