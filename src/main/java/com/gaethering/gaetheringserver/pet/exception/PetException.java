package com.gaethering.gaetheringserver.pet.exception;

import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;
import lombok.Getter;

@Getter
public class PetException extends RuntimeException {

    private final PetErrorCode errorCode;

    protected PetException(PetErrorCode petErrorCode) {
        super(petErrorCode.getMessage());
        this.errorCode = petErrorCode;
    }
}