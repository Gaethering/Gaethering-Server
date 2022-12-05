package com.gaethering.gaetheringserver.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmEmailResponse {

    @JsonProperty("isEmailAuth")
    private boolean emailAuth;

}
