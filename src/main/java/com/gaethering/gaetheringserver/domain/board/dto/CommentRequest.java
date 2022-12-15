package com.gaethering.gaetheringserver.domain.board.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "댓글은 필수 입력사항입니다.")
    private String content;
}
