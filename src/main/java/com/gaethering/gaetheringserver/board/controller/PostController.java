package com.gaethering.gaetheringserver.board.controller;

import com.gaethering.gaetheringserver.board.dto.PostRequest;
import com.gaethering.gaetheringserver.board.dto.PostResponse;
import com.gaethering.gaetheringserver.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/boards")
    public ResponseEntity<PostResponse> writePost
            (@RequestPart(value = "data") PostRequest request,
             @RequestPart(value = "images", required = false) List<MultipartFile> files,
             Principal principal) {

        String email = principal.getName();
        PostResponse response = postService.writePost(email, files, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}