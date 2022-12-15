package com.gaethering.gaetheringserver.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {

    private String content;

    private String nickname;

    private LocalDateTime createAt;
}
