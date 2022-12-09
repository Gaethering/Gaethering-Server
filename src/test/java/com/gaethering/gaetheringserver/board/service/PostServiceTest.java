package com.gaethering.gaetheringserver.board.service;

import com.gaethering.gaetheringserver.board.domain.Category;
import com.gaethering.gaetheringserver.board.domain.Post;
import com.gaethering.gaetheringserver.board.dto.PostRequest;
import com.gaethering.gaetheringserver.board.dto.PostResponse;
import com.gaethering.gaetheringserver.board.exception.CategoryNotFoundException;
import com.gaethering.gaetheringserver.board.exception.errorCode.PostErrorCode;
import com.gaethering.gaetheringserver.board.repository.CategoryRepository;
import com.gaethering.gaetheringserver.board.repository.PostRepository;
import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;

import com.gaethering.gaetheringserver.util.upload.ImageUploader;
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

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ImageUploader imageUploader;

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
    void writePost_Fail_NoCategory () {
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

        CategoryNotFoundException exception = assertThrows( CategoryNotFoundException.class,
        ()-> postService.writePost("test@gmail.com", null, request));

        assertEquals(PostErrorCode.CATEGORY_NOT_EXIST, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("MultipartFile 리스트 ImgUrl 리스트로 변환")
    void changeMultipartFileList_toPostImageList() {

        List<MultipartFile> imageFiles = List.of(
                new MockMultipartFile("test1", "test1.PNG",
                        MediaType.IMAGE_PNG_VALUE, "test1".getBytes(StandardCharsets.UTF_8))
        );

        given(imageUploader.uploadImage(any()))
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
                = postService.writePost(anyString(), null, request);

        assertEquals(0, response.getImageUrls().size());
        assertEquals("닉네임", response.getNickname());
        assertEquals("제목입니다", response.getTitle());
        verify(postRepository, times(1)).save(captor.capture());
    }
}