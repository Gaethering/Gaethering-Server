package com.gaethering.gaetheringserver.board.exception;

import com.gaethering.gaetheringserver.board.exception.errorCode.PostErrorCode;

public class CategoryNotFoundException  extends PostException {

    public CategoryNotFoundException() {
        super(PostErrorCode.CATEGORY_NOT_EXIST);
    }
}