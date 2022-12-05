package com.gaethering.gaetheringserver.pet.exception;

import com.gaethering.modulemember.exception.errorcode.PetErrorCode;

public class InvalidImageTypeException extends PetException {

    public InvalidImageTypeException() {
        super(PetErrorCode.INVALID_IMAGE_TYPE);
    }
}
