package com.gaethering.gaetheringserver.pet.service;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.exception.ImageNotFoundException;
import com.gaethering.gaetheringserver.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.ImageUploader;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class PetServiceImpl implements PetService {

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

        Pet pet = petRepository.findById(id)
            .orElseThrow(PetNotFoundException::new);

        if (!defaultImageUrl.equals(pet.getImageUrl())) {
            imageUploader.removeImage(pet.getImageUrl());
        }

        String newImageUrl = imageUploader.uploadImage(multipartFile);
        pet.updateImage(newImageUrl);

        return newImageUrl;
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
}
