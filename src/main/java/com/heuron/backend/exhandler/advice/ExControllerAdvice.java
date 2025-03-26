package com.heuron.backend.exhandler.advice;

import com.heuron.backend.exception.PatientException;
import com.heuron.backend.exhandler.ErrorResult;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.heuron.backend")
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult internalServerHandler(Exception e) {
        log.error("[INTERNAL_SERVER_ERROR] ex", e);
        return new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "내부 오류");
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResult>  entityNotFoundHandler(EntityNotFoundException e) {
        log.error("[exceptionHandler] ex", e);
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResult(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResult> HttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("[HttpMediaTypeNotSupportedException] ex", e);
        return  ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorResult(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        log.error("[MethodArgumentNotValidException] ex", e);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("message", "유효성 검사 실패");

        errorDetails.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDetails);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResult> httpMessageNotReadableHandler(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadableException]", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResult( HttpStatus.BAD_REQUEST.value(),"잘못된 JSON 형식입니다"));
    }
}
