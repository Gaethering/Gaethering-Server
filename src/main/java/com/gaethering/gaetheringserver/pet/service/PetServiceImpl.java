package com.gaethering.gaetheringserver.pet.service;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterRequest;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterResponse;
import com.gaethering.gaetheringserver.pet.exception.ExceedRegistrablePetException;
import com.gaethering.gaetheringserver.pet.exception.ImageNotFoundException;
import com.gaethering.gaetheringserver.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.upload.ImageUploader;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class PetServiceImpl implements PetService {

    private static final int MAX_REGISTRABLE_PET = 3;

    private final ImageUploader imageUploader;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    private final String defaultImageUrl;

    public PetServiceImpl(ImageUploader imageUploader,
        PetRepository petRepository, MemberRepository memberRepository,
        @Value("${default.image-url}") String defaultImageUrl) {
        this.imageUploader = imageUploader;
        this.petRepository = petRepository;
        this.memberRepository = memberRepository;
        this.defaultImageUrl = defaultImageUrl;
    }

    @Override
    public String updatePetImage(Long id, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new ImageNotFoundException();
        }
        Pet pet = petRepository.findById(id).orElseThrow(PetNotFoundException::new);
        if (!defaultImageUrl.equals(pet.getImageUrl())) {
            imageUploader.removeImage(pet.getImageUrl());
        }
        String newImageUrl = imageUploader.uploadImage(multipartFile);
        pet.updateImage(newImageUrl);
        return newImageUrl;
    }

    @Override
    @Transactional(readOnly = true)
    public PetProfileResponse getPetProfile(Long id) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(PetNotFoundException::new);

        return PetProfileResponse.fromEntity(pet);
    }

    @Override
    public boolean setRepresentativePet(String email, Long petId) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);
        List<Pet> pets = member.getPets();
        pets.forEach(pet -> pet.setRepresentative(false));
        pets.stream().filter(pet -> pet.getId().equals(petId)).findFirst()
            .ifPresent(pet -> pet.setRepresentative(true));
        return true;
    }

    @Override
    @Transactional
    public PetRegisterResponse registerPet(String email, MultipartFile multipartFile,
        PetRegisterRequest petRegisterRequest
    ) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);

        if (member.getPets().size() == MAX_REGISTRABLE_PET) {
            throw new ExceedRegistrablePetException();
        }

        String imageUrl = imageUploader.uploadImage(multipartFile);

        Pet newPet = Pet.builder()
            .name(petRegisterRequest.getPetName())
            .birth(LocalDate.parse(petRegisterRequest.getPetBirth()))
            .weight(petRegisterRequest.getWeight())
            .breed(petRegisterRequest.getBreed())
            .gender(Gender.valueOf(petRegisterRequest.getPetGender()))
            .isNeutered(petRegisterRequest.isNeutered())
            .description(petRegisterRequest.getDescription())
            .imageUrl(imageUrl)
            .isRepresentative(false)
            .build();

        member.addPet(newPet);

        petRepository.save(newPet);

        return PetRegisterResponse.builder()
            .petName(newPet.getName())
            .imageUrl(newPet.getImageUrl())
            .build();
    }
}
