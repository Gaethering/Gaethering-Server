package com.gaethering.gaetheringserver.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostImageUploadResponse {

	private Long imageId;
	private String imageUrl;

	@JsonProperty("isRepresentative")
	private boolean representative;

	private LocalDateTime createdAt;
}
