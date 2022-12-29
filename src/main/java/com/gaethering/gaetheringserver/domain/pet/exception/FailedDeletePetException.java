package com.gaethering.gaetheringserver.domain.pet.exception;

import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;

public class FailedDeletePetException extends PetException {

    public FailedDeletePetException() {
        super(PetErrorCode.FAILED_DELETE_PET);
    }
}
