package com.gaethering.gaetheringserver.pet.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PetErrorCode {

    IMAGE_NOT_FOUND("E1001", "사진이 존재하지 않습니다."),
    INVALID_IMAGE_TYPE("E1002", "사진 형식이 잘못되었습니다."),
    FAILED_UPLOAD_IMAGE("E1003", "사진 업로드에 실패하였습니다."),
    PET_NOT_FOUND("E1004", "반려동물이 존재하지 않습니다."),
    EXCEED_REGISTRABLE_PET("E1005", "반려동물은 최대 3마리를 넘을 수 없습니다.");

    private final String code;
    private final String message;
}
