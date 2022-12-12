package com.gaethering.gaetheringserver.domain.member.dto.mypage;

import com.gaethering.gaetheringserver.domain.board.entity.Post;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MyPostInfo {

    private Long postId;
    private String title;
    private LocalDateTime createdAt;

    public static MyPostInfo of(Post post) {
        return MyPostInfo.builder()
            .postId(post.getId())
            .title(post.getTitle())
            .createdAt(post.getCreatedAt())
            .build();
    }
}
