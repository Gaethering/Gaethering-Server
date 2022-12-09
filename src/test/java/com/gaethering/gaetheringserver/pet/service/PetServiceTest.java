package com.gaethering.gaetheringserver.pet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.exception.member.MemberException;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.dto.PetImageUpdateResponse;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.dto.PetProfileUpdateRequest;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterRequest;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterResponse;
import com.gaethering.gaetheringserver.pet.exception.ExceedRegistrablePetException;
import com.gaethering.gaetheringserver.pet.exception.FailedDeletePetException;
import com.gaethering.gaetheringserver.pet.exception.FailedDeleteRepresentativeException;
import com.gaethering.gaetheringserver.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.upload.ImageUploader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        pets = new ArrayList<>();
        pets.add(pet1);
        pets.add(pet2);
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
        Pet pet = Pet.builder()
            .id(1L)
            .name("해")
            .isRepresentative(true)
            .imageUrl("test")
            .build();
        given(petRepository.findById(anyLong()))
            .willReturn(Optional.of(pet));

        willDoNothing().given(imageUploader).removeImage(anyString());

        String filename = "test.txt";
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
            "test".getBytes());
        given(imageUploader.uploadImage(file))
            .willReturn(file.getName());

        // when
        PetImageUpdateResponse petImageUpdateResponse = petService.updatePetImage(anyLong(), file);

        // then
        assertThat(petImageUpdateResponse.getImageUrl()).isEqualTo(file.getName());
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
        PetProfileUpdateRequest request = PetProfileUpdateRequest.builder()
            .weight(3.7f)
            .neutered(true)
            .description("깨발랄함")
            .build();
        Pet pet = Pet.builder()
            .id(1L)
            .name("해")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("하얗고 귀여움")
            .imageUrl("test")
            .build();

        given(petRepository.findById(anyLong()))
            .willReturn(Optional.of(pet));

        // when
        PetProfileResponse response = petService.updatePetProfile(1L, request);

        // then
        assertThat(response.getWeight()).isEqualTo(pet.getWeight());
        assertThat(response.getDescription()).isEqualTo(pet.getDescription());
        assertThat(response.getName()).isEqualTo(pet.getName());
        assertThat(response.getBirth()).isEqualTo(pet.getBirth());
        assertThat(response.getBreed()).isEqualTo(pet.getBreed());
        assertThat(response.getImageUrl()).isEqualTo(pet.getImageUrl());
    }

    @Test
    @DisplayName("반려동물 프로필 삭제 실패_대표 반려동물 설정 회원 못 찾음")
    void deletePetFailure_MemberNotFound() {
        // given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        // when
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
            () -> petService.deletePetProfile("test@test.com", anyLong()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("반려동물 프로필 삭제 실패_반려동물 1마리일 경우")
    void deletePetFailure_AtLeastOneMorePet() {
        // given
        Pet pet = Pet.builder()
            .id(1L)
            .name("해")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("하얗고 귀여움")
            .imageUrl("test")
            .build();
        pets = new ArrayList<>();
        pets.add(pet);

        Member member = Member.builder()
            .id(1L)
            .email("gaethering@gmail.com")
            .pets(pets)
            .build();
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        // when
        FailedDeletePetException exception = assertThrows(FailedDeletePetException.class,
            () -> petService.deletePetProfile("test@test.com", anyLong()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PetErrorCode.FAILED_DELETE_PET);
    }

    @Test
    @DisplayName("반려동물 프로필 삭제 실패_해당 반려동물 존재하지 않을 경우")
    void deletePetFailure_PetNotFound() {
        // given
        Pet pet1 = Pet.builder()
            .id(1L)
            .name("해")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("하얗고 귀여움")
            .imageUrl("test")
            .build();
        Pet pet2 = Pet.builder()
            .id(2L)
            .name("해2")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(false)
            .description("하얗고 귀여움")
            .imageUrl("test")
            .build();
        pets = new ArrayList<>();
        pets.add(pet1);
        pets.add(pet2);

        Member member = Member.builder()
            .id(1L)
            .email("gaethering@gmail.com")
            .pets(pets)
            .build();
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        // when
        PetNotFoundException exception = assertThrows(PetNotFoundException.class,
            () -> petService.deletePetProfile("test@test.com", 3L));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PetErrorCode.PET_NOT_FOUND);
    }

    @Test
    @DisplayName("반려동물 프로필 삭제 실패_대표 반려동물일 경우")
    void deletePetFailure_Representative() {
        // given
        Pet pet1 = Pet.builder()
            .id(1L)
            .name("해")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("하얗고 귀여움")
            .imageUrl("test")
            .build();
        Pet pet2 = Pet.builder()
            .id(2L)
            .name("해2")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(false)
            .description("하얗고 귀여움")
            .imageUrl("test")
            .build();
        pets = new ArrayList<>();
        pets.add(pet1);
        pets.add(pet2);

        Member member = Member.builder()
            .id(1L)
            .email("gaethering@gmail.com")
            .pets(pets)
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        // when
        FailedDeleteRepresentativeException exception = assertThrows(
            FailedDeleteRepresentativeException.class,
            () -> petService.deletePetProfile("test@test.com", pet1.getId()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PetErrorCode.FAILED_DELETE_REPRESENTATIVE);
    }

    @Test
    void deletePetSuccess() {
        // given
        Pet pet1 = Pet.builder()
            .id(1L)
            .name("해")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("하얗고 귀여움")
            .imageUrl("test")
            .build();
        Pet pet2 = Pet.builder()
            .id(2L)
            .name("해2")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(false)
            .description("하얗고 귀여움")
            .imageUrl("test")
            .build();
        pets = new ArrayList<>();
        pets.add(pet1);
        pets.add(pet2);

        Member member = Member.builder()
            .id(1L)
            .email("gaethering@gmail.com")
            .pets(pets)
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));
        willDoNothing().given(imageUploader).removeImage(anyString());

        // when
        boolean result = petService.deletePetProfile("test@test.com", 2L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("펫 등록 성공")
    void petRegister_Success() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@test.com")
            .pets(pets).build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        MockMultipartFile file = new MockMultipartFile("test", "test.txt", "image/png",
            "test".getBytes());

        given(imageUploader.uploadImage(any()))
            .willReturn(file.getName());

        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);

        //when
        PetRegisterResponse response = petService.registerPet("test@test.com", file,
            request);

        //then
        assertEquals(response.getImageUrl(), file.getName());
        assertEquals(3, member.getPets().size());
        verify(petRepository, times(1)).save(captor.capture());
    }

    @Test
    @DisplayName("펫 등록 시 유저를 찾을 수 없는 경우")
    void petRegister_ExceptionThrown_MemberNotFound() {
        //given
        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        MockMultipartFile file = new MockMultipartFile("test", "test.txt", "image/png",
            "test".getBytes());

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        MemberNotFoundException exception = assertThrows(
            MemberNotFoundException.class,
            () -> petService.registerPet("test@test.com", file, request));

        //then
        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("펫이 이미 3마리 등록되어 있는 경우")
    void petRegister_ExceptionThrown_PetCannotExceedThree() {
        //given
        Pet pet3 = Pet.builder()
            .id(2L).name("pet2")
            .isRepresentative(false).build();

        pets.add(pet3);

        Member member = Member.builder()
            .id(1L)
            .email("test@test.com")
            .pets(pets).build();

        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        MockMultipartFile file = new MockMultipartFile("test", "test.txt", "image/png",
            "test".getBytes());

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        //when
        ExceedRegistrablePetException exception = assertThrows(
            ExceedRegistrablePetException.class,
            () -> petService.registerPet("test@test.com", file, request));

        //then
        assertEquals(PetErrorCode.EXCEED_REGISTRABLE_PET, exception.getErrorCode());
    }
}