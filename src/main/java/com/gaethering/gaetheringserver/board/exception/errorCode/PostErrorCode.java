package com.gaethering.gaetheringserver.board.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostErrorCode {

    POST_NOT_EXIST("E801","존재하지 않는 게시물입니다."),
    CANNOT_UPDATE_POST("E802", "게시물 수정 권한은 작성자에게 있습니다.");

    private final String code;
    private final String message;
}
