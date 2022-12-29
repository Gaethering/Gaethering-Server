package com.gaethering.gaetheringserver.domain.board.controller;

import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
import com.gaethering.gaetheringserver.domain.board.dto.CommentsGetResponse;
import com.gaethering.gaetheringserver.domain.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/boards/{postId}/comments")
    public ResponseEntity<CommentResponse> writeComment (Principal principal,
                                                         @PathVariable Long postId,
                                                         @RequestBody @Valid CommentRequest request) {
        CommentResponse response
                = commentService.writeComment(principal.getName(), postId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/boards/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(Principal principal,
                                                         @PathVariable Long postId,
                                                         @PathVariable Long commentId,
                                                         @RequestBody CommentRequest request) {

        CommentResponse response =
                commentService.updateComment(principal.getName(), postId, commentId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/boards/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(Principal principal,
                                              @PathVariable Long postId,
                                              @PathVariable Long commentId) {

        commentService.deleteComment(principal.getName(), postId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/boards/{postId}/comments")
    public ResponseEntity<CommentsGetResponse> getCommentsList (@PathVariable Long postId,
                                                                @RequestParam int size,
                                                                @RequestParam Long lastCommentId, Principal principal) {

        CommentsGetResponse response
                = commentService.getCommentsByPost(principal.getName(), postId, size, lastCommentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);    }
}
