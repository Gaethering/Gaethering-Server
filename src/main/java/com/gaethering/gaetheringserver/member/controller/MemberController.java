package com.gaethering.gaetheringserver.member.controller;

import com.gaethering.gaetheringserver.member.dto.ConfirmEmailRequest;
import com.gaethering.gaetheringserver.member.dto.ConfirmEmailResponse;
import com.gaethering.gaetheringserver.member.dto.EmailAuthRequest;
import com.gaethering.gaetheringserver.member.dto.OtherProfileResponse;
import com.gaethering.gaetheringserver.member.dto.OwnProfileResponse;
import com.gaethering.gaetheringserver.member.dto.SignUpRequest;
import com.gaethering.gaetheringserver.member.service.MemberProfileService;
import com.gaethering.gaetheringserver.member.service.MemberService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberProfileService memberProfileService;

    @PostMapping("/members/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {

        memberService.signUp(signUpRequest);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/members/email-auth")
    public ResponseEntity<Void> sendEmailAuthCode(@RequestBody EmailAuthRequest emailAuthRequest) {
        memberService.sendEmailAuthCode(emailAuthRequest.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/members/email-confirm")
    public ResponseEntity<ConfirmEmailResponse> confirmEmailAuthCode(
        @RequestBody ConfirmEmailRequest confirmEmailRequest
    ) {
        memberService.confirmEmailAuthCode(confirmEmailRequest.getCode());

        return ResponseEntity.ok(ConfirmEmailResponse.builder()
            .emailAuth(true)
            .build());
    }

    @GetMapping("/members/{memberId}/profile")
    public ResponseEntity<OtherProfileResponse> getOtherProfile(@PathVariable Long memberId) {
        OtherProfileResponse otherProfile = memberProfileService.getOtherProfile(memberId);
        return ResponseEntity.ok(otherProfile);
    }

    @GetMapping("/mypage")
    public ResponseEntity<OwnProfileResponse> getOwnProfile(Principal principal) {
        OwnProfileResponse ownProfile = memberProfileService.getOwnProfile(principal.getName());
        return ResponseEntity.ok(ownProfile);
    }
}
