package com.gaethering.gaetheringserver.domain.board.dto;

import com.gaethering.gaetheringserver.domain.board.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostGetImageUrlResponse {

    private Long imageId;

    private String imageUrl;

    public static PostGetImageUrlResponse fromEntity (PostImage postImage) {

        return PostGetImageUrlResponse.builder()
                .imageId(postImage.getId())
                .imageUrl(postImage.getImageUrl())
                .build();
    }
}
