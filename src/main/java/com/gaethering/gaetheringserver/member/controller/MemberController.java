package com.gaethering.gaetheringserver.member.controller;

import com.gaethering.gaetheringserver.member.dto.*;
import com.gaethering.gaetheringserver.member.service.MemberProfileService;
import com.gaethering.gaetheringserver.member.service.MemberService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        @RequestBody ConfirmEmailRequest confirmEmailRequest) {
        memberService.confirmEmailAuthCode(confirmEmailRequest.getCode());

        return ResponseEntity.ok(ConfirmEmailResponse.builder()
            .emailAuth(true)
            .build());
    }

    @GetMapping("/members/{memberId}/profile")
    public ResponseEntity<OtherProfileResponse> getOtherProfile(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberProfileService.getOtherProfile(memberId));
    }

    @GetMapping("/mypage")
    public ResponseEntity<OwnProfileResponse> getOwnProfile(Principal principal) {
        return ResponseEntity.ok(memberProfileService.getOwnProfile(principal.getName()));
    }

    @PatchMapping("/mypage/nickname")
    public ResponseEntity<ModifyMemberNicknameResponse> modifyMemberNickname(
        @RequestBody String nickname, Principal principal) {
        String email = principal.getName();
        memberService.modifyNickname(email, nickname);
        return ResponseEntity.ok(new ModifyMemberNicknameResponse(nickname));
    }

    @PostMapping("/members/auth/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest request) {
        LoginResponse loginResponse = memberService.login(request);
        return ResponseEntity.ok(loginResponse);
    }
    @PostMapping("/members/auth/reissue-token")
    public ResponseEntity<ReissueTokenResponse> reissueAccessToken
    (@RequestBody ReissueTokenRequest request) {
        ReissueTokenResponse tokenResponse = memberService.reissue(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> reissueAccessToken
    (@RequestBody LogoutRequest request) {
        memberService.logout(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
