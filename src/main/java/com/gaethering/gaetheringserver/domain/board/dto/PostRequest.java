package com.gaethering.gaetheringserver.domain.board.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Long categoryId;

}
