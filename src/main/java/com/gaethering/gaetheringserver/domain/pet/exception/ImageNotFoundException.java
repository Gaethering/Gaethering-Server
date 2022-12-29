package com.gaethering.gaetheringserver.domain.pet.exception;

import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;

public class ImageNotFoundException extends PetException {

    public ImageNotFoundException() {
        super(PetErrorCode.IMAGE_NOT_FOUND);
    }
}
