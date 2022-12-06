package com.gaethering.gaetheringserver.pet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.MemberException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.util.ImageUploader;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}