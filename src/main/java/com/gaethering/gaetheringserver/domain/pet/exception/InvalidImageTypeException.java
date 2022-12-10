package com.gaethering.gaetheringserver.domain.pet.exception;


import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;

public class InvalidImageTypeException extends PetException {

    public InvalidImageTypeException() {
        super(PetErrorCode.INVALID_IMAGE_TYPE);
    }
}
