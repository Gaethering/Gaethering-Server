package com.gaethering.gaetheringserver.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostWriteImageUrlResponse {

    private String imageUrl;

    @JsonProperty("isRepresentative")
    private boolean representative;
}
