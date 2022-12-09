package com.gaethering.gaetheringserver.board.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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