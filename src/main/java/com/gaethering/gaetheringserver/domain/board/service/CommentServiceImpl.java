package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.CommentDetailResponse;
import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
import com.gaethering.gaetheringserver.domain.board.dto.CommentsGetResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Comment;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.exception.CommentNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionDeleteCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionUpdateCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.repository.CommentRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.board.util.ScrollPagingUtil;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

        post.writeComment(comment);

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

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException());

        if (!Objects.equals(member, comment.getMember())) {
            throw new NoPermissionDeleteCommentException();
        }
        commentRepository.delete(comment);
        post.deleteComment(comment);

        return true;
    }

    @Override
    public CommentsGetResponse getCommentsByPost(String email, Long postId, int size, long lastCommentId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());

        PageRequest pageRequest = PageRequest.of(0, size + 1);
        List<Comment> comments
                = commentRepository.findAllByPostAndIdIsLessThanOrderByIdDesc(post, lastCommentId, pageRequest);

        List<CommentDetailResponse> commentResponses = new ArrayList<>();

        for(Comment comment : comments) {
            CommentDetailResponse response = CommentDetailResponse.fromEntity(comment);
            if(email.equals(comment.getMember().getEmail())) {
                response.setOwner(true);
            } else {
                response.setOwner(false);
            }
            commentResponses.add(response);
        }

        ScrollPagingUtil<CommentDetailResponse> commentsCursor
                = ScrollPagingUtil.of(commentResponses, size);

        return CommentsGetResponse.of(commentsCursor, commentRepository.countByPost(post));
    }
}
