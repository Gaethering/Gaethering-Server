package com.gaethering.gaetheringserver.domain.member.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyMemberNicknameRequest {

    private String nickname;
}
