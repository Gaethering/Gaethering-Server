package com.gaethering.gaetheringserver.domain.board.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {

	@NotBlank(message = "게시글 제목은 필수 입력사항입니다.")
	private String title;

	@NotBlank(message = "게시글 내용은 필수 입력사항입니다.")
	private String content;
}
