package com.gaethering.gaetheringserver.domain.pet.service;

import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.domain.aws.s3.S3Service;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.domain.pet.dto.PetImageUpdateResponse;
import com.gaethering.gaetheringserver.domain.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.domain.pet.dto.PetProfileUpdateRequest;
import com.gaethering.gaetheringserver.domain.pet.dto.PetRegisterRequest;
import com.gaethering.gaetheringserver.domain.pet.dto.PetRegisterResponse;
import com.gaethering.gaetheringserver.domain.pet.entity.Pet;
import com.gaethering.gaetheringserver.domain.pet.exception.ExceedRegistrablePetException;
import com.gaethering.gaetheringserver.domain.pet.exception.FailedDeletePetException;
import com.gaethering.gaetheringserver.domain.pet.exception.FailedDeleteRepresentativeException;
import com.gaethering.gaetheringserver.domain.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.domain.pet.repository.PetRepository;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private static final int MIN_EXIST_PET = 1;
    private static final int MAX_REGISTRABLE_PET = 3;

    private final S3Service s3Service;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    @Override
    public PetImageUpdateResponse updatePetImage(Long id, MultipartFile multipartFile) {
        Pet pet = petRepository.findById(id).orElseThrow(PetNotFoundException::new);

        s3Service.removeImage(pet.getImageUrl());

        String newImageUrl = s3Service.uploadImage(multipartFile);
        pet.updateImage(newImageUrl);

        return PetImageUpdateResponse.builder().imageUrl(newImageUrl).build();
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
    public PetProfileResponse updatePetProfile(Long id, PetProfileUpdateRequest request) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(PetNotFoundException::new);

        pet.updatePetProfile(request.getWeight(), request.isNeutered(), request.getDescription());

        return PetProfileResponse.fromEntity(pet);
    }

    @Override
    public boolean deletePetProfile(String email, Long id) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        if (member.getPets().size() == MIN_EXIST_PET) {
            throw new FailedDeletePetException();
        }

        Pet findPet = member.getPets().stream().filter(pet -> pet.getId().equals(id)).findFirst()
                .orElseThrow(PetNotFoundException::new);

        if (findPet.isRepresentative()) {
            throw new FailedDeleteRepresentativeException();
        }

        s3Service.removeImage(findPet.getImageUrl());

        petRepository.delete(findPet);

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

        String imageUrl = s3Service.uploadImage(multipartFile);

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
