package com.gaethering.gaetheringserver.domain.pet.exception;

import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;

public class FailedDeleteRepresentativeException extends PetException {

    public FailedDeleteRepresentativeException() {
        super(PetErrorCode.FAILED_DELETE_REPRESENTATIVE);
    }
}
