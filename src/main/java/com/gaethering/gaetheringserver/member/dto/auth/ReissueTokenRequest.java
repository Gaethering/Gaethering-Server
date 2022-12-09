package com.gaethering.gaetheringserver.member.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReissueTokenRequest {

    private String accessToken;

    private String refreshToken;
}
