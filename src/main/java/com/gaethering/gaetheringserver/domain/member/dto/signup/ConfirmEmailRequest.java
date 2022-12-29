package com.gaethering.gaetheringserver.domain.member.dto.signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmEmailRequest {

    private String code;

}
