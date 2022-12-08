package com.gaethering.gaetheringserver.pet.exception;

import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;

public class ExceedRegistrablePetException extends PetException {

    public ExceedRegistrablePetException() {
        super(PetErrorCode.EXCEED_REGISTRABLE_PET);
    }
}
