package com.gaethering.gaetheringserver.pet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.MemberException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.member.type.Gender;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.ImageUploader;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    ImageUploader imageUploader;
    @Mock
    private PetRepository petRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private PetServiceImpl petService;
    private List<Pet> pets;

    @BeforeEach
    public void setUp() {
        Pet pet1 = Pet.builder()
            .id(1L).name("pet1")
            .isRepresentative(true).build();
        Pet pet2 = Pet.builder()
            .id(2L).name("pet2")
            .isRepresentative(false).build();
        Pet pet3 = Pet.builder()
            .id(3L).name("pet3")
            .isRepresentative(false).build();
        pets = List.of(pet1, pet2, pet3);
    }

    @Test
    @DisplayName("대표 반려동물 설정 회원 못 찾았을 때")
    public void setRepresentativePetFailure() {
        //given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberException.class,
            () -> petService.setRepresentativePet("test@test.com", 1L));
    }

    @Test
    public void setRepresentativePetSuccess() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@test.com")
            .pets(pets).build();
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        //when
        boolean result = petService.setRepresentativePet(member.getEmail(), 2L);

        //then
        assertThat(result).isTrue();
        pets.forEach(pet -> {
            if (pet.getId().equals(2L)) {
                assertThat(pet.isRepresentative()).isTrue();
            } else {
                assertThat(pet.isRepresentative()).isFalse();
            }
        });
    }

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
            .imageUrl("test")
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

    @Test
    @DisplayName("반려동물 사진 수정 실패_반려동물 존재하지 않음")
    void updatePetImageFailure_PetNotFound() {
        // given
        String filename = "test.txt";
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
            "test".getBytes());

        given(petRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        PetNotFoundException exception = assertThrows(PetNotFoundException.class,
            () -> petService.updatePetImage(anyLong(), file));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PetErrorCode.PET_NOT_FOUND);
    }

    @Test
    void updatePetImageSuccess() {
        // given
        String filename = "test.txt";
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
            "test".getBytes());
        given(imageUploader.uploadImage(file))
            .willReturn(file.getName());

        Pet pet = Pet.builder()
            .id(1L)
            .name("해")
            .isRepresentative(true)
            .imageUrl("test")
            .build();
        given(petRepository.findById(anyLong()))
            .willReturn(Optional.of(pet));

        // when
        String imageUrl = petService.updatePetImage(anyLong(), file);

        // then
        assertThat(imageUrl).isEqualTo(file.getName());
    }

    @Test
    @DisplayName("반려동물 프로필 수정 실패_해당 반려동물 프로필 없을때")
    void updatePetProfileFailure() {
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
    void updatePetProfileSuccess() {
        // given
        Pet pet = Pet.builder()
            .id(1L)
            .name("해")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("하얗고 귀여움")
            .imageUrl(
                "https://gaethering.s3.ap-northeast-2.amazonaws.com/default/%EA%B0%95%EC%95%84%EC%A7%803.jpeg")
            .build();
        given(petRepository.findById(anyLong()))
            .willReturn(Optional.of(pet));

        float weight = 4.1f;
        boolean isNeutered = true;
        String description = "깨발랄함";

        // when
        PetProfileResponse response = petService.updatePetProfile(1L, weight, isNeutered, description);

        // then
        assertThat(response.getWeight()).isEqualTo(weight);
        assertThat(response.getDescription()).isEqualTo(description);
        assertThat(response.getName()).isEqualTo(pet.getName());
        assertThat(response.getBirth()).isEqualTo(pet.getBirth());
        assertThat(response.getBreed()).isEqualTo(pet.getBreed());
        assertThat(response.getImageUrl()).isEqualTo(pet.getImageUrl());
    }


}