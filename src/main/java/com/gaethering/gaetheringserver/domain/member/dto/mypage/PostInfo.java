package com.gaethering.gaetheringserver.domain.member.dto.mypage;

import com.gaethering.gaetheringserver.domain.board.entity.Post;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostInfo {

    private Long postId;
    private String title;
    private LocalDateTime createdAt;

    public static PostInfo of(Post post) {
        return PostInfo.builder()
            .postId(post.getId())
            .title(post.getTitle())
            .createdAt(post.getCreatedAt())
            .build();
    }
}
