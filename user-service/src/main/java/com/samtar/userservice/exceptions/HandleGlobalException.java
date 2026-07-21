package com.samtar.userservice.exceptions;
import com.samtar.dto.ExceptionApiResponse;
import com.samtar.dto.ValidationErrorResponse;
import com.samtar.exception.BaseException;
import com.samtar.exception.ValidationException;
import com.samtar.userservice.constants.MessageConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class HandleGlobalException {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(HttpRequestMethodNotSupportedException err) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ExceptionApiResponse<>(null, MessageConstant.METHOD_NOT_ALLOWED, LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(HttpMessageNotReadableException err) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionApiResponse<>(null, MessageConstant.INVALID_JSON, LocalDateTime.now()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse<Map<String, String>>> handleGlobalError(ValidationException err) {
        return ResponseEntity.status((HttpStatusCode) err.getStatusCode())
                .body(new ValidationErrorResponse<>(err.getErrors(), err.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(BaseException err) {
        return ResponseEntity.status((HttpStatusCode) err.getStatusCode())
                .body(new ExceptionApiResponse<>(null, err.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse<Map<String, String>>> handleGlobalError(
            MethodArgumentNotValidException err) {

        Map<String, String> errors = new HashMap<>();
        err.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse<>(errors, MessageConstant.INVALID_PAYLOAD, LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(Exception err) {
        System.out.println("Error: " + err.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionApiResponse<>(null, MessageConstant.FAIL_TO_EXECUTE, LocalDateTime.now()));
    }

}
