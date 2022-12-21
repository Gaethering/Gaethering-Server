package com.gaethering.gaetheringserver.domain.board.exception.errorCode;

import com.gaethering.gaetheringserver.core.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements ErrorCode {

    POST_NOT_FOUND("E801", "존재하지 않는 게시물입니다."),
    CATEGORY_NOT_FOUND("E802", "존재하지 않는 카테고리입니다."),
    COMMENT_NOT_FOUND("E803", "존재하지 않는 댓글입니다."),
    NO_PERMISSION_TO_UPDATE_COMMENT("E804", "댓글 수정 권한은 작성자에게 있습니다."),
    NO_PERMISSION_TO_DELETE_COMMENT("E805", "댓글 삭제 권한은 작성자에게 있습니다."),
    NO_PERMISSION_TO_UPDATE_POST("E806", "게시물 수정 권한은 작성자에게 있습니다."),
    POST_IMAGE_NOT_FOUND("E807", "해당 게시물 이미지가 존재하지 않습니다."),
    NO_PERMISSION_TO_DELETE_POST("E808", "게시물 삭제 권한은 작성자에게 있습니다.");

    private final String code;
    private final String message;
}