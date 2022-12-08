package com.gaethering.gaetheringserver.board.dto;

import com.gaethering.gaetheringserver.board.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    private String title;

    private String content;

    private Category category;

}
