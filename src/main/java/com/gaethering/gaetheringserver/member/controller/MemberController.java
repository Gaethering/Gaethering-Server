package com.gaethering.gaetheringserver.member.controller;

import com.gaethering.gaetheringserver.member.dto.ConfirmEmailRequest;
import com.gaethering.gaetheringserver.member.dto.ConfirmEmailResponse;
import com.gaethering.gaetheringserver.member.dto.EmailAuthRequest;
import com.gaethering.gaetheringserver.member.dto.SignUpRequest;
import com.gaethering.gaetheringserver.member.dto.SignUpResponse;
import com.gaethering.gaetheringserver.member.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
        @RequestPart("image") MultipartFile multipartFile,
        @RequestPart("data") @Valid SignUpRequest signUpRequest
    ) {

        SignUpResponse response = memberService.signUp(multipartFile, signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
    }

    @PostMapping("/email-auth")
    public ResponseEntity<Void> sendEmailAuthCode(@RequestBody EmailAuthRequest emailAuthRequest) {
        memberService.sendEmailAuthCode(emailAuthRequest.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/email-confirm")
    public ResponseEntity<ConfirmEmailResponse> confirmEmailAuthCode(
        @RequestBody ConfirmEmailRequest confirmEmailRequest
    ) {
        memberService.confirmEmailAuthCode(confirmEmailRequest.getCode());

        return ResponseEntity.ok(ConfirmEmailResponse.builder()
            .emailAuth(true)
            .build());
    }
}
