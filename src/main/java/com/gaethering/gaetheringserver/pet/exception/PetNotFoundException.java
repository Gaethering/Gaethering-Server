package com.gaethering.gaetheringserver.pet.exception;

import com.gaethering.modulemember.exception.errorcode.PetErrorCode;

public class PetNotFoundException extends PetException {

    public PetNotFoundException() {
        super(PetErrorCode.PET_NOT_FOUND);
    }
}
