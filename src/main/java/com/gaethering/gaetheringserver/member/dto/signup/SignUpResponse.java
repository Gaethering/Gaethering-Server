package com.gaethering.gaetheringserver.member.dto.signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignUpResponse {

    private String petName;

    private String imageUrl;

}
