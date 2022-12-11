package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Comment;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.exception.CommentNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.FailDeleteCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.FailUpdateCommentException;
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
                .comment(request.getComment())
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .comment(comment.getComment())
                .nickname(comment.getMember().getNickname())
                .createAt(comment.getCreatedAt())
                .build();
    }
    @Override
    @Transactional
    public CommentResponse updateComment (String email, Long postId, Long commentId,
                                          CommentRequest request) {

        boolean result = postRepository.existsById(postId);

        if(!result) {
            throw new PostNotFoundException();
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CommentNotFoundException());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        if(!Objects.equals(member, comment.getMember())) {
            throw new FailUpdateCommentException();
        }
        comment.setComment(request.getComment());
        commentRepository.save(comment);

        return CommentResponse.builder()
                .comment(comment.getComment())
                .nickname(comment.getMember().getNickname())
                .createAt(comment.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public boolean deleteComment (String email, Long postId, Long commentId) {

        boolean result = postRepository.existsById(postId);

        if(!result) {
            throw new PostNotFoundException();
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CommentNotFoundException());

        if(!Objects.equals(member, comment.getMember())) {
            throw new FailDeleteCommentException();
        }
        commentRepository.delete(comment);

        return true;
    }
}
