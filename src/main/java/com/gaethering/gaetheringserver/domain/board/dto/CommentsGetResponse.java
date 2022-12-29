package com.gaethering.gaetheringserver.domain.board.dto;

import com.gaethering.gaetheringserver.domain.board.util.ScrollPagingUtil;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentsGetResponse {

    private static final long LAST_CURSOR = -1L;

    private List<CommentDetailResponse> comments = new ArrayList<>();

    private long totalCommentsCnt;

    private long nextCursor;


    public static CommentsGetResponse of (ScrollPagingUtil<CommentDetailResponse> commentsScroll, long totalCommentsCnt) {
        if (commentsScroll.isLastScroll()) {
            return CommentsGetResponse.newLastScroll(commentsScroll.getCurrentScrollItems(), totalCommentsCnt);
        }
        return CommentsGetResponse.newScrollHasNext(commentsScroll.getCurrentScrollItems(), totalCommentsCnt, commentsScroll.getNextCursor().getCommentId());

    }
    private static CommentsGetResponse newLastScroll(List<CommentDetailResponse> commentsScroll, long totalCommentsCnt) {
        return new CommentsGetResponse(commentsScroll, totalCommentsCnt, LAST_CURSOR);
    }
    private static CommentsGetResponse newScrollHasNext(List<CommentDetailResponse> commentsScroll, long totalCommentsCnt, long nextCursor) {
        return new CommentsGetResponse(commentsScroll, totalCommentsCnt, nextCursor);
    }
}
