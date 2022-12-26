package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
import com.gaethering.gaetheringserver.domain.board.dto.CommentsGetResponse;

public interface CommentService {

    CommentResponse writeComment(String email, Long postId, CommentRequest request);

    CommentResponse updateComment (String email, Long postId, Long commentId, CommentRequest request);

    boolean deleteComment (String email, Long postId, Long commentId);

    CommentsGetResponse getCommentsByPost (String email, Long postId, int size, long lastCommentId);
}
