package com.gaethering.gaetheringserver.pet.exception;

import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;

public class RepresentativePetNotFoundException extends PetException {

    public RepresentativePetNotFoundException() {
        super(PetErrorCode.REPRESENTATIVE_PET_NOT_FOUND);
    }
}
