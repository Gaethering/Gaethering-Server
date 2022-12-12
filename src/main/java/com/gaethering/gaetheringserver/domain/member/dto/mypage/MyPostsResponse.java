package com.gaethering.gaetheringserver.domain.member.dto.mypage;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MyPostsResponse {

    private int postCount;

    private List<MyPostInfo> posts;

}
