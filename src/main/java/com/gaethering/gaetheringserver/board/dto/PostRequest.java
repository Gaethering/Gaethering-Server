package com.gaethering.gaetheringserver.board.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostRequest {

    private String title;

    private String content;

    private Long categoryId;

}
