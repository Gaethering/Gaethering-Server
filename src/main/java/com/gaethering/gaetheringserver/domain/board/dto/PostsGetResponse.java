package com.gaethering.gaetheringserver.domain.board.dto;

import com.gaethering.gaetheringserver.domain.board.util.ScrollPagingUtil;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostsGetResponse {

    private static final long LAST_CURSOR = -1L;

    private List<PostDetailResponse> posts = new ArrayList<>();

    private long totalPostsCnt;

    private long nextCursor;

    public static PostsGetResponse of (ScrollPagingUtil<PostDetailResponse> postsScroll, long totalPostsCnt) {
        if (postsScroll.isLastScroll()) {
            return PostsGetResponse.newLastScroll(postsScroll.getCurrentScrollItems(), totalPostsCnt);
        }
        return PostsGetResponse.newScrollHasNext(postsScroll.getCurrentScrollItems(), totalPostsCnt, postsScroll.getNextCursor().getPostId());

    }
    private static PostsGetResponse newLastScroll(List<PostDetailResponse> postsScroll, long totalPostsCnt) {
        return new PostsGetResponse(postsScroll, totalPostsCnt, LAST_CURSOR);
    }
    private static PostsGetResponse newScrollHasNext(List<PostDetailResponse> postsScroll, long totalPostsCnt, long nextCursor) {
        return new PostsGetResponse(postsScroll, totalPostsCnt, nextCursor);
    }
}