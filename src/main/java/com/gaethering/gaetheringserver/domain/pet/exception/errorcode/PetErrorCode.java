package com.gaethering.gaetheringserver.domain.pet.exception.errorcode;

import com.gaethering.gaetheringserver.core.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PetErrorCode implements ErrorCode {

    IMAGE_NOT_FOUND("E201", "사진이 존재하지 않습니다."),
    INVALID_IMAGE_TYPE("E202", "사진 형식이 잘못되었습니다."),
    FAILED_UPLOAD_IMAGE("E203", "사진 업로드에 실패하였습니다."),
    PET_NOT_FOUND("E204", "반려동물이 존재하지 않습니다."),
    REPRESENTATIVE_PET_NOT_FOUND("E205", "대표 반려동물이 존재하지 않습니다."),
    EXCEED_REGISTRABLE_PET("E206", "반려동물은 최대 3마리를 넘을 수 없습니다."),
    FAILED_DELETE_PET("E207", "반려동물은 1마리 이상이어야 합니다."),
    FAILED_DELETE_REPRESENTATIVE("E208", "대표 반려동물은 삭제할 수 없습니다.");

    private final String code;
    private final String message;
}
