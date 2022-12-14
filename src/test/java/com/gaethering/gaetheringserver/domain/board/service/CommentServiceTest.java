package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
import com.gaethering.gaetheringserver.domain.board.dto.CommentsGetResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Comment;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.exception.CommentNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionDeleteCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionUpdateCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;
import com.gaethering.gaetheringserver.domain.board.repository.CommentRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("?????? ?????? ??????")
    void writeCommentSuccess() {

        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Post post = Post.builder()
                .id(1L)
                .title("??????")
                .content("??????")
                .comments(new ArrayList<>())
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        CommentRequest request = CommentRequest.builder()
                .content("???????????????")
                .build();

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        CommentResponse response =
                commentService.writeComment("test@gmail.com", 1L, request);

        verify(commentRepository, times(1)).save(captor.capture());
        assertEquals("???????????????", response.getContent());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ??????")
    void writeCommentFail_NoUser() {

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        CommentRequest request = CommentRequest.builder()
                .content("???????????????")
                .build();

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> commentService.writeComment("test@gmail.com", 1L, request));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ??????")
    void writeCommentFail_NoPost() {

        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CommentRequest request = CommentRequest.builder()
                .content("???????????????")
                .build();

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> commentService.writeComment("test@gmail.com", 1L, request));

        assertEquals(PostErrorCode.POST_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void updateCommentSuccess() {

        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Post post = Post.builder()
                .id(1L)
                .title("??????")
                .content("??????")
                .build();

        given(postRepository.existsById(anyLong()))
                .willReturn(true);

        Comment comment = Comment.builder()
                .id(1L)
                .post(post)
                .member(member)
                .content("???????????????")
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        CommentRequest request = CommentRequest.builder()
                .content("????????? ???????????????")
                .build();

        CommentResponse response =
                commentService.updateComment("test@gmail.com", 1L, 1L, request);

        assertEquals("????????? ???????????????", response.getContent());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ??????")
    void updateCommentFail_NoPost() {

        given(postRepository.existsById(anyLong()))
                .willReturn(false);

        CommentRequest request = CommentRequest.builder()
                .content("????????? ???????????????")
                .build();

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> commentService.updateComment("test@gmail.com", 1L, 1L, request));

        assertEquals(PostErrorCode.POST_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ??????")
    void updateCommentFail_NoComment() {

        given(postRepository.existsById(anyLong()))
                .willReturn(true);

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CommentRequest request = CommentRequest.builder()
                .content("????????? ???????????????")
                .build();

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class,
                () -> commentService.updateComment("test@gmail.com", 1L, 1L, request));

        assertEquals(PostErrorCode.COMMENT_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ??????")
    void updateCommentFail_NoUser() {

        given(postRepository.existsById(anyLong()))
                .willReturn(true);

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        Comment comment = Comment.builder()
                .id(1L)
                .content("???????????????")
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        CommentRequest request = CommentRequest.builder()
                .content("????????? ???????????????")
                .build();

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> commentService.updateComment("test@gmail.com", 1L, 1L, request));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
    void updateCommentFail_UNMATCH_writer() {

        given(postRepository.existsById(anyLong()))
                .willReturn(true);

        Member member1 = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail("test@gmail.com"))
                .willReturn(Optional.of(member1));

        Member member2 = Member.builder()
                .id(2L)
                .email("update@gmail.com")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .content("???????????????")
                .member(member2)
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        CommentRequest request = CommentRequest.builder()
                .content("????????? ???????????????")
                .build();

        NoPermissionUpdateCommentException exception = assertThrows(NoPermissionUpdateCommentException.class,
                () -> commentService.updateComment("test@gmail.com", 1L, 1L, request));

        assertEquals(PostErrorCode.NO_PERMISSION_TO_UPDATE_COMMENT, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void deleteCommentSuccess() {

        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Post post = Post.builder()
                .id(1L)
                .title("??????")
                .content("??????")
                .comments(new ArrayList<>())
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        Comment comment = Comment.builder()
                .id(1L)
                .content("???????????????")
                .member(member)
                .post(post)
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        boolean result = commentService.deleteComment("test@gmail.com", 1L, 1L);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ??????")
    void deleteCommentFail_NoPost() {

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> commentService.deleteComment("test@gmail.com", 1L, 1L));

        assertEquals(PostErrorCode.POST_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ??????")
    void deleteCommentFail_NoUser() {

        Post post = Post.builder()
                .id(1L)
                .title("??????")
                .content("??????")
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> commentService.deleteComment("test@gmail.com", 1L, 1L));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ??????")
    void deleteCommentFail_NoComment() {

        Post post = Post.builder()
                .id(1L)
                .title("??????")
                .content("??????")
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));
        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class,
                () -> commentService.deleteComment("test@gmail.com", 1L, 1L));

        assertEquals(PostErrorCode.COMMENT_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
    void deleteCommentFail_UNMATCH_writer() {

        Post post = Post.builder()
                .id(1L)
                .title("??????")
                .content("??????")
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        Member member1 = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        given(memberRepository.findByEmail("test@gmail.com"))
                .willReturn(Optional.of(member1));

        Member member2 = Member.builder()
                .id(2L)
                .email("update@gmail.com")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .content("???????????????")
                .member(member2)
                .post(post)
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        NoPermissionDeleteCommentException exception = assertThrows(NoPermissionDeleteCommentException.class,
                () -> commentService.deleteComment("test@gmail.com", 1L, 1L));

        assertEquals(PostErrorCode.NO_PERMISSION_TO_DELETE_COMMENT, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void getCommentsByPost_Success () {

        Member member1 = Member.builder()
                .email("test111@gmail.com")
                .nickname("?????????11")
                .build();

        Member member2 = Member.builder()
                .email("test222@gmail.com")
                .nickname("?????????22")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("??????")
                .content("??????")
                .build();

        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        Comment comment1 = Comment.builder()
                .id(1L)
                .post(post)
                .member(member1)
                .content("????????? ??????")
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .post(post)
                .member(member2)
                .content("????????? ??????")
                .build();

        given(commentRepository.findAllByPostAndIdIsLessThanOrderByIdDesc(any(), anyLong(), any(PageRequest.class)))
                .willReturn(List.of(comment1, comment2));

        CommentsGetResponse response = commentService.getCommentsByPost("test@gmail.com", 1L, 5, 10);

        assertEquals(2, response.getComments().size());
        assertEquals(comment1.getContent(), response.getComments().get(0).getContent());
        assertEquals(comment2.getContent(), response.getComments().get(1).getContent());
        assertEquals(comment1.getMember().getNickname(), response.getComments().get(0).getNickname());
        assertEquals(comment2.getMember().getNickname(), response.getComments().get(1).getNickname());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ??????")
    void getCommentsByPost_Fail_NoPost () {

        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> commentService.getCommentsByPost("test@gmail.com", 1L, 5, 10));

        assertEquals(PostErrorCode.POST_NOT_FOUND, exception.getPostErrorCode());
    }
}