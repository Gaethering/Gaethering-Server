package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Comment;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.exception.CommentNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionDeleteCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionUpdateCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.repository.CommentRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public CommentResponse writeComment(String email, Long postId, CommentRequest request) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());

        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .memberId(comment.getMember().getId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public CommentResponse updateComment(String email, Long postId, Long commentId,
                                         CommentRequest request) {

        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        if (!Objects.equals(member, comment.getMember())) {
            throw new NoPermissionUpdateCommentException();
        }
        comment.setComment(request.getContent());
        commentRepository.save(comment);

        return CommentResponse.builder()
                .memberId(comment.getMember().getId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public boolean deleteComment(String email, Long postId, Long commentId) {

        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException());

        if (!Objects.equals(member, comment.getMember())) {
            throw new NoPermissionDeleteCommentException();
        }
        commentRepository.delete(comment);

        return true;
    }
}
