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
import com.gaethering.gaetheringserver.domain.aws.s3.S3Service;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberException;
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
import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;
import com.gaethering.gaetheringserver.domain.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.domain.pet.service.PetServiceImpl;
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
    S3Service s3Service;
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
    @DisplayName("?????? ???????????? ?????? ?????? ??? ????????? ???")
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
    @DisplayName("???????????? ????????? ?????? ??????_???????????? ???????????? ??????")
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
    @DisplayName("???????????? ????????? ?????? ??????")
    void getPetProfileSuccess() {
        // given
        Pet pet = Pet.builder()
            .id(1L)
            .name("???")
            .birth(LocalDate.of(2021, 12, 5))
            .gender(Gender.FEMALE)
            .breed("?????????")
            .weight(3.6f)
            .isNeutered(true)
            .isRepresentative(true)
            .description("????????? ?????????")
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
    @DisplayName("???????????? ?????? ?????? ??????_???????????? ???????????? ??????")
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
            .name("???")
            .isRepresentative(true)
            .imageUrl("test")
            .build();
        given(petRepository.findById(anyLong()))
            .willReturn(Optional.of(pet));

        String filename = "test.txt";
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
            "test".getBytes());

        willDoNothing().given(s3Service).removeImage(anyString(), anyString());
        given(s3Service.uploadImage(any(), anyString())).willReturn(file.getName());

        // when
        PetImageUpdateResponse petImageUpdateResponse = petService.updatePetImage(1L, any());

        // then
        assertThat(petImageUpdateResponse.getImageUrl()).isEqualTo(file.getName());
    }

    @Test
    @DisplayName("???????????? ????????? ?????? ??????_?????? ???????????? ????????? ?????????")
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
            .description("????????????")
            .build();
        Pet pet = Pet.builder()
            .id(1L)
            .name("???")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("????????? ?????????")
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
    @DisplayName("???????????? ????????? ?????? ??????_?????? ???????????? ?????? ?????? ??? ??????")
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
    @DisplayName("???????????? ????????? ?????? ??????_???????????? 1????????? ??????")
    void deletePetFailure_AtLeastOneMorePet() {
        // given
        Pet pet = Pet.builder()
            .id(1L)
            .name("???")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("????????? ?????????")
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
    @DisplayName("???????????? ????????? ?????? ??????_?????? ???????????? ???????????? ?????? ??????")
    void deletePetFailure_PetNotFound() {
        // given
        Pet pet1 = Pet.builder()
            .id(1L)
            .name("???")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("????????? ?????????")
            .imageUrl("test")
            .build();
        Pet pet2 = Pet.builder()
            .id(2L)
            .name("???2")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(false)
            .description("????????? ?????????")
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
    @DisplayName("???????????? ????????? ?????? ??????_?????? ??????????????? ??????")
    void deletePetFailure_Representative() {
        // given
        Pet pet1 = Pet.builder()
            .id(1L)
            .name("???")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("????????? ?????????")
            .imageUrl("test")
            .build();
        Pet pet2 = Pet.builder()
            .id(2L)
            .name("???2")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(false)
            .description("????????? ?????????")
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
            .name("???")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(true)
            .description("????????? ?????????")
            .imageUrl("test")
            .build();
        Pet pet2 = Pet.builder()
            .id(2L)
            .name("???2")
            .weight(3.6f)
            .isNeutered(false)
            .isRepresentative(false)
            .description("????????? ?????????")
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
        willDoNothing().given(s3Service).removeImage(anyString(), anyString());

        // when
        boolean result = petService.deletePetProfile("test@test.com", 2L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("??? ?????? ??????")
    void petRegister_Success() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@test.com")
            .pets(pets).build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("??????")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("?????????")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();

        MockMultipartFile file = new MockMultipartFile("test", "test.txt", "image/png",
            "test".getBytes());

        given(s3Service.uploadImage(any(), anyString()))
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
    @DisplayName("??? ?????? ??? ????????? ?????? ??? ?????? ??????")
    void petRegister_ExceptionThrown_MemberNotFound() {
        //given
        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("??????")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("?????????")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
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
    @DisplayName("?????? ?????? 3?????? ???????????? ?????? ??????")
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
            .petName("??????")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("?????????")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
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