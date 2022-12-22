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
    @DisplayName("회원 가입 성공")
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
    @DisplayName("회원가입 실패_중복된 이메일이 있을 경우")
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
    @DisplayName("닉네임 수정 회원 없을 때 실패")
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
    @DisplayName("로그인 정보 제공 성공")
    void getLoginInfo_Success() {
        //given
        Pet pet1 = Pet.builder()
            .name("하울")
            .imageUrl("testurl1")
            .isRepresentative(false)
            .build();
        Pet pet2 = Pet.builder()
            .name("여울")
            .imageUrl("testurl2")
            .isRepresentative(true)
            .build();

        pets = List.of(pet1, pet2);

        Member member = Member.builder()
            .email("test@test.com")
            .nickname("내캉")
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
    @DisplayName("로그인 정보 제공 실패_사용자 못 찾는 경우")
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
    @DisplayName("로그인 정보 제공 실패_대표 반려동물 못 찾는 경우")
    void getLoginInfo_ExceptionThrown_RepresentativePetNotFound() {
        //given
        Pet pet1 = Pet.builder()
            .name("하울")
            .imageUrl("testurl")
            .isRepresentative(false)
            .build();
        Pet pet2 = Pet.builder()
            .name("하울")
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
    @DisplayName("내가 쓴 글 조회 성공")
    void getMyPosts_Success() {
        //given
        Member member = Member.builder()
            .email("test@test.com")
            .nickname("내캉")
            .build();

        Post post1 = Post.builder()
            .title("제목1")
            .category(Category.builder()
                .id(1L)
                .categoryName("카테고리")
                .build())
            .member(member)
            .build();

        Post post2 = Post.builder()
            .title("제목2")
            .category(Category.builder()
                .id(1L)
                .categoryName("카테고리")
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
    @DisplayName("내가 쓴 글 조회 실패_사용자 못 찾는 경우")
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
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("김진호")
            .phone("010-3230-2498")
            .birth("2017-03-15")
            .gender("MALE")
            .emailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();
    }
}