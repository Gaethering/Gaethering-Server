package com.gaethering.gaetheringserver.domain.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gaethering.gaetheringserver.domain.aws.s3.S3Service;
import com.gaethering.gaetheringserver.domain.board.dto.PostRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Category;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.entity.PostImage;
import com.gaethering.gaetheringserver.domain.board.exception.CategoryNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionUpdatePostException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;
import com.gaethering.gaetheringserver.domain.board.repository.CategoryRepository;
import com.gaethering.gaetheringserver.domain.board.repository.HeartRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private HeartRepository heartRepository;
    @Mock
    private S3Service s3Service;
    @InjectMocks
    private PostServiceImpl postService;


    @Test
    @DisplayName("게시물 작성 실패 - 회원 찾을 수 없는 경우")
    void writePost_Fail_NoUser() {

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        PostRequest request = PostRequest.builder()
            .title("제목입니다")
            .content("내용입니다")
            .categoryId(1L)
            .build();

        MemberNotFoundException exception = assertThrows(
            MemberNotFoundException.class,
            () -> postService.writePost(anyString(), null, request));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시물 작성 실패 - 카테고리 찾을 수 없는 경우")
    void writePost_Fail_NoCategory() {
        Member member = Member.builder()
            .id(1L)
            .email("email@gmail.com")
            .nickname("닉네임")
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        given(categoryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        PostRequest request = PostRequest.builder()
            .title("제목입니다")
            .content("내용입니다")
            .categoryId(1L)
            .build();

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
            () -> postService.writePost("test@gmail.com", null, request));

        assertEquals(PostErrorCode.CATEGORY_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("MultipartFile 리스트 ImgUrl 리스트로 변환")
    void changeMultipartFileList_toPostImageList() {

        List<MultipartFile> imageFiles = List.of(
            new MockMultipartFile("test1", "test1.PNG",
                MediaType.IMAGE_PNG_VALUE, "test1".getBytes(StandardCharsets.UTF_8))
        );

        given(s3Service.uploadImage(any()))
            .willReturn(imageFiles.get(0).getName());

        List<String> imgUrlList = postService.getImageUrlsInRequest(imageFiles);
        assertEquals(1, imgUrlList.size());
        assertEquals("test1", imgUrlList.get(0));
    }

    @Test
    @DisplayName("게시물 작성 성공")
    void writePost_Success() {

        Member member = Member.builder()
            .id(1L)
            .email("email@gmail.com")
            .nickname("닉네임")
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        Category category = Category.builder()
            .id(1L)
            .categoryName("카테고리")
            .build();

        given(categoryRepository.findById(anyLong()))
            .willReturn(Optional.of(category));

        PostRequest request = PostRequest.builder()
            .title("제목입니다")
            .content("내용입니다")
            .categoryId(1L)
            .build();

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

        PostResponse response
            = postService.writePost(anyString(), new ArrayList<>(), request);

        assertEquals(0, response.getImageUrls().size());
        assertEquals("닉네임", response.getNickname());
        assertEquals("제목입니다", response.getTitle());
        verify(postRepository, times(1)).save(captor.capture());
    }

    @Test
    @DisplayName("게시글 수정 실패-회원 찾을 수 없는 경우")
    void updatePostFailure_MemberNotFound() {
        // given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        PostUpdateRequest request = PostUpdateRequest.builder()
            .title("게시글 제목 수정")
            .content("게시글 내용 수정")
            .build();

        // when
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
            () -> postService.updatePost("test@test.com", anyLong(), request));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 수정 실패-게시물 찾을 수 없는 경우")
    void updatePostFailure_PostNotFound() {
        // given
        Member member = Member.builder()
            .id(1L)
            .email("gaethering@gmail.com")
            .build();
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));
        given(postRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        PostUpdateRequest request = PostUpdateRequest.builder()
            .title("게시글 제목 수정")
            .content("게시글 내용 수정")
            .build();

        // when
        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
            () -> postService.updatePost(member.getEmail(), 1L, request));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 수정 실패-게시물 작성자가 아닐 경우")
    void updatePostFailure_NoPermissionUpdatePost() {
        // given
        Member member1 = Member.builder()
            .id(1L)
            .email("gaethering@gmail.com")
            .build();
        Member member2 = Member.builder()
            .id(2L)
            .email("test@gmail.com")
            .build();
        Post post = Post.builder()
            .id(1L)
            .title("게시물 제목")
            .content("게시물 내용")
            .member(member1)
            .build();
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member1));
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member2));
        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));

        PostUpdateRequest request = PostUpdateRequest.builder()
            .title("게시글 제목 수정")
            .content("게시글 내용 수정")
            .build();

        // when
        NoPermissionUpdatePostException exception = assertThrows(NoPermissionUpdatePostException.class,
            () -> postService.updatePost(member2.getEmail(), 1L, request));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorCode.NO_PERMISSION_TO_UPDATE_POST);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updatePostSuccess() {
        // given
        Member member1 = Member.builder()
            .id(1L)
            .email("gaethering@gmail.com")
            .nickname("닉네임")
            .build();
        Category category = Category.builder()
            .id(1L)
            .categoryName("질문 있어요")
            .build();
        PostImage postImage = PostImage.builder()
            .id(1L)
            .isRepresentative(false)
            .imageUrl("https://test~")
            .build();
        Post post = Post.builder()
            .id(1L)
            .title("게시물 제목")
            .content("게시물 내용")
            .member(member1)
            .postImages(List.of(postImage))
            .category(category)
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member1));
        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        given(heartRepository.countByPost(post))
            .willReturn(1L);

        PostUpdateRequest request = PostUpdateRequest.builder()
            .title("게시글 제목 수정")
            .content("게시글 내용 수정")
            .build();

        // when
        PostUpdateResponse response = postService.updatePost(member1.getEmail(), 1L, request);

        // then
        assertThat(response.getTitle()).isEqualTo(post.getTitle());
        assertThat(response.getContent()).isEqualTo(post.getContent());
        assertThat(response.getCreatedAt()).isEqualTo(post.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(post.getUpdatedAt());
    }
}