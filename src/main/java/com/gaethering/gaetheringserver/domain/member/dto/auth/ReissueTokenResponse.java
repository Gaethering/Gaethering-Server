package com.gaethering.gaetheringserver.domain.member.dto.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReissueTokenResponse {

    private String accessToken;

}