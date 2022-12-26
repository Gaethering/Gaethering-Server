package com.gaethering.gaetheringserver.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gaethering.gaetheringserver.domain.board.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentDetailResponse {

    private Long commentId;

    private Long memberId;

    private String content;

    private String nickname;

    @JsonProperty(value = "isOwner")
    private boolean owner;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public static CommentDetailResponse fromEntity (Comment comment) {

        return CommentDetailResponse.builder()
                .memberId(comment.getMember().getId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
