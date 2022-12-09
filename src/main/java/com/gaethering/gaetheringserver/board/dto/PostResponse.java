package com.gaethering.gaetheringserver.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private String title;

    private String content;

    private List<String> imageUrls;

    private String categoryName;

    private int viewCnt;

    private int heartCnt;

    private String nickname;

    private LocalDateTime createAt;
}
