package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.HeartResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Heart;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;
import com.gaethering.gaetheringserver.domain.board.repository.HeartRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HeartServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private HeartRepository heartRepository;

    @InjectMocks
    private HeartServiceImpl heartService;

    @Test
    @DisplayName("좋아요 누르기 성공")
    void pushHeart_Success () {

        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .hearts(new ArrayList<>())
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        ArgumentCaptor<Heart> captor = ArgumentCaptor.forClass(Heart.class);

        HeartResponse response = heartService.pushHeart(1L, "test@gmail.com");

        verify(heartRepository, times(1)).save(captor.capture());
        assertEquals(1L, response.getPostId());
        assertEquals(1L, response.getMemberId());
    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 게시물 없음")
    void pushHeart_Fail_NoPost () {

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> heartService.pushHeart(1L, "test@gmail.com"));

        assertEquals(PostErrorCode.POST_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 회원 없음")
    void pushHeart_Fail_NoMember () {

        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .hearts(new ArrayList<>())
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> heartService.pushHeart(1L, "test@gmail.com"));
        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    @DisplayName("좋아요 누르기 (취소) 성공")
    void pushHeart_toCancel_Success () {

        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .hearts(new ArrayList<>())
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        Heart heart = Heart.builder()
                .id(1L)
                .post(post)
                .member(member)
                .build();

        given(heartRepository.findByPostAndMember(post, member))
                .willReturn(Optional.of(heart));

        HeartResponse response = heartService.pushHeart(1L, "test@gmail.com");
        assertEquals(1L, response.getPostId());
        assertEquals(0, response.getHeartCnt());
    }
}