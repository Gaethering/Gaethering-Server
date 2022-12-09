package com.gaethering.gaetheringserver.pet.exception;

import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;

public class FailedDeleteRepresentativeException extends PetException {

    public FailedDeleteRepresentativeException() {
        super(PetErrorCode.FAILED_DELETE_REPRESENTATIVE);
    }
}
