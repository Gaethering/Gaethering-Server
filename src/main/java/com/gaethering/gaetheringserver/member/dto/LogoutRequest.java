package com.gaethering.gaetheringserver.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogoutRequest {

    private String accessToken;

    private String refreshToken;
}
