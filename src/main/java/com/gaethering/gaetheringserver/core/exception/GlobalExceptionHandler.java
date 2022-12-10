package com.gaethering.gaetheringserver.core.exception;

import com.gaethering.gaetheringserver.domain.member.exception.auth.MemberAuthException;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(e.getErrorCode().getCode())
            .message(e.getMessage())
            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ErrorResponse> handleMailSendException(MailSendException e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(MemberErrorCode.FAILED_SEND_EMAIL.getCode())
            .message(MemberErrorCode.FAILED_SEND_EMAIL.getMessage())
            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(MemberErrorCode.INVALID_ARGUMENT.getCode())
            .message(Objects.requireNonNull(e.getFieldError()).getDefaultMessage())
            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MemberAuthException.class)
    public ResponseEntity<ErrorResponse> handleMemberAuthException(MemberAuthException e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(e.getErrorCode().getCode())
            .message(e.getMessage())
            .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

}
