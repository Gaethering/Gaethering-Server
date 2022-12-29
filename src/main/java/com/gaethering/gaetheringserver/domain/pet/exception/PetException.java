package com.gaethering.gaetheringserver.domain.pet.exception;

import com.gaethering.gaetheringserver.core.exception.BusinessException;
import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;
import lombok.Getter;

@Getter
public class PetException extends BusinessException {

    private final PetErrorCode errorCode;

    protected PetException(PetErrorCode petErrorCode) {
        super(petErrorCode);
        this.errorCode = petErrorCode;
    }
}