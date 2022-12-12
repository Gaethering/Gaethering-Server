package com.gaethering.gaetheringserver.domain.board.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentRequest {

    @NotBlank
    private String comment;
}
