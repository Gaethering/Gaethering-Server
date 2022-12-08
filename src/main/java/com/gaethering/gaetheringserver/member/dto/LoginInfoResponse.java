package com.gaethering.gaetheringserver.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfoResponse {

    private String petName;

    private String imageUrl;

    private String nickname;

}
