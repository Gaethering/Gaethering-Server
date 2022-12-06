package com.gaethering.gaetheringserver.member.controller;

import com.gaethering.gaetheringserver.member.dto.ConfirmEmailRequest;
import com.gaethering.gaetheringserver.member.dto.ConfirmEmailResponse;
import com.gaethering.gaetheringserver.member.dto.EmailAuthRequest;
import com.gaethering.gaetheringserver.member.dto.ModifyMemberNicknameResponse;
import com.gaethering.gaetheringserver.member.dto.SignUpRequest;
import com.gaethering.gaetheringserver.member.service.MemberService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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

    @PatchMapping("/mypage/nickname")
    public ResponseEntity<ModifyMemberNicknameResponse> modifyMemberNickname(
        @RequestBody String nickname, Principal principal) {
        String email = principal.getName();
        memberService.modifyNickname(email, nickname);
        return ResponseEntity.ok(new ModifyMemberNicknameResponse(nickname));
    }
}
