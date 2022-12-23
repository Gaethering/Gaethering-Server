package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
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
    @DisplayName("댓글 작성 성공")
    void writeCommentSuccess() {

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
                .comments(new ArrayList<>())
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        CommentResponse response =
                commentService.writeComment("test@gmail.com", 1L, request);

        verify(commentRepository, times(1)).save(captor.capture());
        assertEquals("댓글입니다", response.getContent());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 회원 없음")
    void writeCommentFail_NoUser() {

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> commentService.writeComment("test@gmail.com", 1L, request));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 게시물 없음")
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
                .content("댓글입니다")
                .build();

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> commentService.writeComment("test@gmail.com", 1L, request));

        assertEquals(PostErrorCode.POST_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateCommentSuccess() {

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
                .build();

        given(postRepository.existsById(anyLong()))
                .willReturn(true);

        Comment comment = Comment.builder()
                .id(1L)
                .post(post)
                .member(member)
                .content("댓글입니다")
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        CommentRequest request = CommentRequest.builder()
                .content("수정한 댓글입니다")
                .build();

        CommentResponse response =
                commentService.updateComment("test@gmail.com", 1L, 1L, request);

        assertEquals("수정한 댓글입니다", response.getContent());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 게시물 없음")
    void updateCommentFail_NoPost() {

        given(postRepository.existsById(anyLong()))
                .willReturn(false);

        CommentRequest request = CommentRequest.builder()
                .content("수정한 댓글입니다")
                .build();

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> commentService.updateComment("test@gmail.com", 1L, 1L, request));

        assertEquals(PostErrorCode.POST_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 없음")
    void updateCommentFail_NoComment() {

        given(postRepository.existsById(anyLong()))
                .willReturn(true);

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CommentRequest request = CommentRequest.builder()
                .content("수정한 댓글입니다")
                .build();

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class,
                () -> commentService.updateComment("test@gmail.com", 1L, 1L, request));

        assertEquals(PostErrorCode.COMMENT_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 회원 없음")
    void updateCommentFail_NoUser() {

        given(postRepository.existsById(anyLong()))
                .willReturn(true);

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        Comment comment = Comment.builder()
                .id(1L)
                .content("댓글입니다")
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        CommentRequest request = CommentRequest.builder()
                .content("수정한 댓글입니다")
                .build();

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> commentService.updateComment("test@gmail.com", 1L, 1L, request));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 수정 권한 없음")
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
                .content("댓글입니다")
                .member(member2)
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        CommentRequest request = CommentRequest.builder()
                .content("수정한 댓글입니다")
                .build();

        NoPermissionUpdateCommentException exception = assertThrows(NoPermissionUpdateCommentException.class,
                () -> commentService.updateComment("test@gmail.com", 1L, 1L, request));

        assertEquals(PostErrorCode.NO_PERMISSION_TO_UPDATE_COMMENT, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteCommentSuccess() {

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
                .comments(new ArrayList<>())
                .build();

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        Comment comment = Comment.builder()
                .id(1L)
                .content("댓글입니다")
                .member(member)
                .post(post)
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        boolean result = commentService.deleteComment("test@gmail.com", 1L, 1L);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 게시물 없음")
    void deleteCommentFail_NoPost() {

        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> commentService.deleteComment("test@gmail.com", 1L, 1L));

        assertEquals(PostErrorCode.POST_NOT_FOUND, exception.getPostErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 회원 없음")
    void deleteCommentFail_NoUser() {

        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
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
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    void deleteCommentFail_NoComment() {

        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
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
    @DisplayName("댓글 삭제 실패 - 삭제 권한 없음")
    void deleteCommentFail_UNMATCH_writer() {

        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
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
                .content("댓글입니다")
                .post(post)
                .member(member2)
                .build();

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        NoPermissionDeleteCommentException exception = assertThrows(NoPermissionDeleteCommentException.class,
                () -> commentService.deleteComment("test@gmail.com", 1L, 1L));

        assertEquals(PostErrorCode.NO_PERMISSION_TO_DELETE_COMMENT, exception.getPostErrorCode());
    }
}