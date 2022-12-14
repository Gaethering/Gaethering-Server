package com.gaethering.gaetheringserver.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gaethering.gaetheringserver.domain.aws.s3.S3Service;
import com.gaethering.gaetheringserver.domain.board.entity.Category;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.member.dto.auth.LoginInfoResponse;
import com.gaethering.gaetheringserver.domain.member.dto.mypage.MyPostsResponse;
import com.gaethering.gaetheringserver.domain.member.dto.signup.SignUpRequest;
import com.gaethering.gaetheringserver.domain.member.dto.signup.SignUpResponse;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.domain.member.exception.member.DuplicatedEmailException;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.domain.member.service.member.MemberServiceImpl;
import com.gaethering.gaetheringserver.domain.pet.entity.Pet;
import com.gaethering.gaetheringserver.domain.pet.exception.RepresentativePetNotFoundException;
import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;
import com.gaethering.gaetheringserver.domain.pet.repository.PetRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MemberServiceImpl memberService;

    private static List<Pet> pets;

    @Test
    @DisplayName("?????? ?????? ??????")
    void signUp_Success() {
        //given
        SignUpRequest request = getSignUpRequest();

        String filename = "test.txt";
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
            "test".getBytes());

        given(s3Service.uploadImage(any(), anyString()))
            .willReturn(file.getName());

        given(memberRepository.existsByEmail(anyString()))
            .willReturn(false);

        given(passwordEncoder.encode(anyString()))
            .willReturn("test");

        ArgumentCaptor<Member> captorMember = ArgumentCaptor.forClass(Member.class);

        ArgumentCaptor<Pet> captorPet = ArgumentCaptor.forClass(Pet.class);

        //when
        SignUpResponse response = memberService.signUp(file, request);

        //then
        assertEquals(response.getImageUrl(), file.getName());
        verify(memberRepository, times(1)).save(captorMember.capture());
        verify(petRepository, times(1)).save(captorPet.capture());
    }

    @Test
    @DisplayName("???????????? ??????_????????? ???????????? ?????? ??????")
    void signUp_ExceptionThrown_DuplicatedEmail() {
        //given
        SignUpRequest request = getSignUpRequest();

        MockMultipartFile file = new MockMultipartFile("test", "test.txt", "image/png",
            "test".getBytes());

        given(memberRepository.existsByEmail(anyString()))
            .willReturn(true);

        //when
        DuplicatedEmailException exception = assertThrows(
            DuplicatedEmailException.class, () -> memberService.signUp(file, request));

        //then
        assertEquals(MemberErrorCode.DUPLICATED_EMAIL.getCode(),
            exception.getErrorCode().getCode());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ??? ??????")
    public void modifyNicknameFailure() {
        //given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        //then
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
            () -> memberService.modifyNickname("test@test.com", "modify email"));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    public void modifyNicknameSuccess() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@test.com")
            .nickname("past nickname")
            .build();
        String modifiedNickname = "modified nickname";
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        //when
        boolean result = memberService.modifyNickname("test@test.com",
            modifiedNickname);

        //then
        assertThat(result).isTrue();
        assertThat(member.getNickname()).isEqualTo(modifiedNickname);
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????")
    void getLoginInfo_Success() {
        //given
        Pet pet1 = Pet.builder()
            .name("??????")
            .imageUrl("testurl1")
            .isRepresentative(false)
            .build();
        Pet pet2 = Pet.builder()
            .name("??????")
            .imageUrl("testurl2")
            .isRepresentative(true)
            .build();

        pets = List.of(pet1, pet2);

        Member member = Member.builder()
            .email("test@test.com")
            .nickname("??????")
            .pets(pets)
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        //when
        LoginInfoResponse response = memberService.getLoginInfo("test@test.com");

        //then
        assertEquals(pet2.getName(), response.getPetName());
        assertEquals(pet2.getImageUrl(), response.getImageUrl());
        assertEquals(member.getNickname(), response.getNickname());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????_????????? ??? ?????? ??????")
    void getLoginInfo_ExceptionThrown_MemberNotFound() {
        //given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        MemberNotFoundException exception = assertThrows(
            MemberNotFoundException.class,
            () -> memberService.getLoginInfo("test@test.com"));

        //then
        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????_?????? ???????????? ??? ?????? ??????")
    void getLoginInfo_ExceptionThrown_RepresentativePetNotFound() {
        //given
        Pet pet1 = Pet.builder()
            .name("??????")
            .imageUrl("testurl")
            .isRepresentative(false)
            .build();
        Pet pet2 = Pet.builder()
            .name("??????")
            .imageUrl("testurl")
            .isRepresentative(false)
            .build();

        pets = List.of(pet1, pet2);

        Member member = Member.builder()
            .pets(pets)
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        //when
        RepresentativePetNotFoundException exception = assertThrows(
            RepresentativePetNotFoundException.class,
            () -> memberService.getLoginInfo("test@test.com"));

        //then
        assertEquals(PetErrorCode.REPRESENTATIVE_PET_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ??? ??? ?????? ??????")
    void getMyPosts_Success() {
        //given
        Member member = Member.builder()
            .email("test@test.com")
            .nickname("??????")
            .build();

        Post post1 = Post.builder()
            .title("??????1")
            .category(Category.builder()
                .id(1L)
                .categoryName("????????????")
                .build())
            .member(member)
            .build();

        Post post2 = Post.builder()
            .title("??????2")
            .category(Category.builder()
                .id(1L)
                .categoryName("????????????")
                .build())
            .member(member)
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        given(postRepository.findAllByMember(any()))
            .willReturn(List.of(post1, post2));

        //when
        MyPostsResponse response = memberService.getMyPosts("test@test.com");

        //then
        assertEquals(2, response.getPostCount());
        assertEquals(post1.getId(), response.getPosts().get(0).getPostId());
        assertEquals(post1.getTitle(), response.getPosts().get(0).getTitle());
        assertEquals(post2.getId(), response.getPosts().get(1).getPostId());
        assertEquals(post2.getTitle(), response.getPosts().get(1).getTitle());
    }

    @Test
    @DisplayName("?????? ??? ??? ?????? ??????_????????? ??? ?????? ??????")
    void getMyPosts_ExceptionThrown_MemberNotFound() {
        //given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        MemberNotFoundException exception = assertThrows(
            MemberNotFoundException.class,
            () -> memberService.getMyPosts("test@test.com"));

        //then
        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    private static SignUpRequest getSignUpRequest() {

        return SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("?????????")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("?????????")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .emailAuth(true)
            .petName("??????")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("?????????")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();
    }
}