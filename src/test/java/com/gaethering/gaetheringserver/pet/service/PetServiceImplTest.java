package com.gaethering.gaetheringserver.pet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetServiceImpl petService;

    @Test
    @DisplayName("반려동물 프로필 조회 실패_반려동물 존재하지 않음")
    void getPetProfileFailure() {
        // given
        given(petRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        PetNotFoundException exception = assertThrows(PetNotFoundException.class,
            () -> petService.getPetProfile(anyLong()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PetErrorCode.PET_NOT_FOUND);
    }

    @Test
    @DisplayName("반려동물 프로필 조회 성공")
    void getPetProfileSuccess() {
        // given
        Pet pet = Pet.builder()
            .id(1L)
            .name("해")
            .birth(LocalDate.of(2021, 12, 5))
            .gender(Gender.FEMALE)
            .breed("말티즈")
            .weight(3.6f)
            .isNeutered(true)
            .isRepresentative(true)
            .description("하얗고 귀여움")
            .imageUrl(
                "https://gaethering.s3.ap-northeast-2.amazonaws.com/default/%EA%B0%95%EC%95%84%EC%A7%803.jpeg")
            .build();
        given(petRepository.findById(anyLong()))
            .willReturn(Optional.of(pet));

        // when
        PetProfileResponse petProfile = petService.getPetProfile(anyLong());

        // then
        assertThat(petProfile.getName()).isEqualTo(pet.getName());
        assertThat(petProfile.getBirth()).isEqualTo(pet.getBirth());
        assertThat(petProfile.getGender()).isEqualTo(pet.getGender());
        assertThat(petProfile.getBreed()).isEqualTo(pet.getBreed());
        assertThat(petProfile.getWeight()).isEqualTo(pet.getWeight());
        assertThat(petProfile.getDescription()).isEqualTo(pet.getDescription());
        assertThat(petProfile.getImageUrl()).isEqualTo(pet.getImageUrl());
    }
}