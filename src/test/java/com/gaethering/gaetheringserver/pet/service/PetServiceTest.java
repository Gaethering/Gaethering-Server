package com.gaethering.gaetheringserver.pet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.MemberException;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterRequest;
import com.gaethering.gaetheringserver.pet.dto.PetRegisterResponse;
import com.gaethering.gaetheringserver.pet.exception.ExceedRegistrablePetException;
import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.ImageUploader;
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