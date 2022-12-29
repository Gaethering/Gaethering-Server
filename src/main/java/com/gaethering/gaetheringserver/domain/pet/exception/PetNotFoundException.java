package com.gaethering.gaetheringserver.domain.pet.exception;

import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;

public class PetNotFoundException extends PetException {

    public PetNotFoundException() {
        super(PetErrorCode.PET_NOT_FOUND);
    }
}
