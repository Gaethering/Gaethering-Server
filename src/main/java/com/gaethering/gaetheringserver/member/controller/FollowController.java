package com.gaethering.gaetheringserver.member.controller;

import com.gaethering.gaetheringserver.member.dto.FollowResponse;
import com.gaethering.gaetheringserver.member.service.FollowService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/members/{memberId}/follow")
    public ResponseEntity<Void> createFollow(@PathVariable Long memberId, Principal principal) {
        followService.createFollow(principal.getName(), memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/members/{memberId}/follower")
    public ResponseEntity<List<FollowResponse>> getFollowers(@PathVariable Long memberId) {
        return ResponseEntity.ok(followService.getFollowers(memberId));
    }

    @GetMapping("/members/{memberId}/following")
    public ResponseEntity<List<FollowResponse>> getFollowings(@PathVariable Long memberId) {
        return ResponseEntity.ok(followService.getFollowees(memberId));
    }
}
