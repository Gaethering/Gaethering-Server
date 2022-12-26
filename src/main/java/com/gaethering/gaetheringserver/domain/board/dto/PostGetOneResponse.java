package com.gaethering.gaetheringserver.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostGetOneResponse {

    private Long postId;

    private String title;

    private String content;

    private String nickname;

    private int heartCnt;

    private long viewCnt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty(value = "isOwner")
    private boolean owner;

    private List<PostGetImageUrlResponse> images;

    public void setOwner(boolean owner) {
        this.owner = owner;
    }
}