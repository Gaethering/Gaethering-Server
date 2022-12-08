package com.gaethering.gaetheringserver.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyMemberNicknameRequest {
    private String nickname;
}
