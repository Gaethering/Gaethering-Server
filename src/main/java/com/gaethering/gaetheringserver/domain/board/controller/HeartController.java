package com.gaethering.gaetheringserver.domain.board.controller;

import com.gaethering.gaetheringserver.domain.board.dto.HeartResponse;
import com.gaethering.gaetheringserver.domain.board.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class HeartController {

    private final HeartService heartService;

    @PostMapping("/boards/{postId}/hearts")
    public ResponseEntity<HeartResponse> pushHeart (@PathVariable Long postId, Principal principal) {

        HeartResponse response = heartService.pushHeart(postId, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/boards/{postId}/hearts")
    public ResponseEntity<HeartResponse> cancelHeart (@PathVariable Long postId, Principal principal) {

        HeartResponse response = heartService.cancelHeart(postId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
