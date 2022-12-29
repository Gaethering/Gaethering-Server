package com.gaethering.gaetheringserver.domain.pet.exception;

import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;

public class RepresentativePetNotFoundException extends PetException {

    public RepresentativePetNotFoundException() {
        super(PetErrorCode.REPRESENTATIVE_PET_NOT_FOUND);
    }
}
