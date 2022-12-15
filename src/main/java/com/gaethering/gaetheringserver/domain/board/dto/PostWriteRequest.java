package com.gaethering.gaetheringserver.domain.board.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostWriteRequest {

    @NotBlank(message = "게시글 제목은 필수 입력사항입니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수 입력사항입니다.")
    private String content;

    private Long categoryId;

}
