package com.gaethering.gaetheringserver.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.entity.PostImage;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostUpdateResponse {

	private String title;
	private String content;
	private String categoryName;
	private int viewCnt;
	private int heartCnt;
	private String nickname;
	private List<PostImageUrlResponse> imageUrls;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static PostUpdateResponse from(Post post, Member member, int heartCnt) {
		List<PostImageUrlResponse> imageUrls = post.getPostImages().stream().map(PostImageUrlResponse::of)
			.collect(Collectors.toList());
		return PostUpdateResponse.builder()
			.title(post.getTitle())
			.content(post.getContent())
			.viewCnt(post.getViewCnt())
			.heartCnt(heartCnt)
			.categoryName(post.getCategory().getCategoryName())
			.nickname(member.getNickname())
			.imageUrls(imageUrls)
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class PostImageUrlResponse {

		private Long imageId;
		private String imageUrl;

		@JsonProperty("isRepresentative")
		private boolean representative;

		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static PostImageUrlResponse of(PostImage postImage) {
			return PostImageUrlResponse.builder()
				.imageId(postImage.getId())
				.imageUrl(postImage.getImageUrl())
				.representative(postImage.isRepresentative())
				.createdAt(postImage.getCreatedAt())
				.updatedAt(postImage.getUpdatedAt())
				.build();
		}
	}
}
