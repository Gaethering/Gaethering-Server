package com.gaethering.gaetheringserver.pet.exception;

import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;

public class FailedDeletePetException extends PetException {

    public FailedDeletePetException() {
        super(PetErrorCode.FAILED_DELETE_PET);
    }
}
