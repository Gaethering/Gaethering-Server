package com.gaethering.gaetheringserver.domain.board.controller;

import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
import com.gaethering.gaetheringserver.domain.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> writeComment (Principal principal,
                                                         @PathVariable Long postId,
                                                         @RequestBody @Valid CommentRequest request) {
        CommentResponse response
                = commentService.writeComment(principal.getName(), postId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(Principal principal,
                                                         @PathVariable Long postId,
                                                         @PathVariable Long commentId,
                                                         @RequestBody CommentRequest request) {

        CommentResponse response =
                commentService.updateComment(principal.getName(), postId, commentId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(Principal principal,
                                              @PathVariable Long postId,
                                              @PathVariable Long commentId) {

        commentService.deleteComment(principal.getName(), postId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
