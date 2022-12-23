package com.gaethering.gaetheringserver.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostDetailResponse {

    private Long postId;

    private String title;

    private String content;

    private String imageUrl;

    private long heartCnt;

    private long commentCnt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static PostDetailResponse fromEntity (Post post) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .heartCnt(post.getHearts().size())
                .commentCnt(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
