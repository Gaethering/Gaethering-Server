package com.gaethering.gaetheringserver.pet.service;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.exception.ImageNotFoundException;
import com.gaethering.gaetheringserver.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.ImageUploader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

	private final ImageUploader imageUploader;
	private final PetRepository petRepository;
	private final MemberRepository memberRepository;

	@Override
	public String updatePetImage(Long id, MultipartFile multipartFile) {
		if (multipartFile.isEmpty()) {
			throw new ImageNotFoundException();
		}
		Pet pet = petRepository.findById(id).orElseThrow(PetNotFoundException::new);

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
}
