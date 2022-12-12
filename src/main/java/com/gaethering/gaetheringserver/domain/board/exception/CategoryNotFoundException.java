package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class CategoryNotFoundException extends PostException {

    public CategoryNotFoundException() {
        super(PostErrorCode.CATEGORY_NOT_FOUND);
    }
}