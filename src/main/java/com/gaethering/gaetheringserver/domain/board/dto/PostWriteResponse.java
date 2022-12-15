package com.gaethering.gaetheringserver.domain.board.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostWriteResponse {

    private String title;

    private String content;

    private List<String> imageUrls;

    private String categoryName;

    private int viewCnt;

    private int heartCnt;

    private String nickname;

    private LocalDateTime createAt;
}