package com.gaethering.gaetheringserver.domain.board.exception.errorCode;

import com.gaethering.gaetheringserver.core.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements ErrorCode {

    POST_NOT_EXIST("E801", "존재하지 않는 게시물입니다."),
    CATEGORY_NOT_EXIST("E802", "존재하지 않는 카테고리입니다."),
    COMMENT_NOT_EXIST("E803", "존재하지 않는 댓글입니다."),
    CANNOT_UPDATE_COMMENT("E804", "댓글 수정 권한은 작성자에게 있습니다."),
    CANNOT_DELETE_COMMENT("E805", "댓글 삭제 권한은 작성자에게 있습니다."),
    CANNOT_UPDATE_POST("E806", "게시물 수정 권한은 작성자에게 있습니다.");

    private final String code;
    private final String message;
}