package com.gaethering.gaetheringserver.pet.service;

import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.exception.ImageNotFoundException;
import com.gaethering.gaetheringserver.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.ImageUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class PetServiceImpl implements PetService {

    private final ImageUploader imageUploadService;
    private final PetRepository petRepository;

    private final String defaultImageUrl;

    public PetServiceImpl(ImageUploader imageUploadService,
        PetRepository petRepository,
        @Value("${default.image-url}") String defaultImageUrl) {
        this.imageUploadService = imageUploadService;
        this.petRepository = petRepository;
        this.defaultImageUrl = defaultImageUrl;
    }

    @Override
    public void updatePetImage(Long id, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new ImageNotFoundException();
        }

        Pet pet = petRepository.findById(id)
            .orElseThrow(PetNotFoundException::new);

        if (!defaultImageUrl.equals(pet.getImageUrl())) {
            imageUploadService.removeImage(pet.getImageUrl());
        }

        pet.updateImage(imageUploadService.uploadImage(multipartFile));
    }

    @Override
    @Transactional(readOnly = true)
    public PetProfileResponse getPetProfile(Long id) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(PetNotFoundException::new);

        return PetProfileResponse.fromEntity(pet);
    }
}
