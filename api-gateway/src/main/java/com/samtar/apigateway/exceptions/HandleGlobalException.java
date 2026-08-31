package com.samtar.apigateway.exceptions;

import com.samtar.apigateway.constants.MessageConstant;
import com.samtar.dto.ExceptionApiResponse;
import com.samtar.dto.ValidationErrorResponse;
import com.samtar.exception.BaseException;
import com.samtar.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class HandleGlobalException {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(BaseException err) {
        return ResponseEntity.status((HttpStatusCode) err.getStatusCode())
                .body(new ExceptionApiResponse<>(null, err.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(HttpMessageNotReadableException err) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionApiResponse<>(null, MessageConstant.BAD_GATEWAY, LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(Exception err) {
        System.out.println("Error: " + err.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionApiResponse<>(null, MessageConstant.FAIL_TO_EXECUTE, LocalDateTime.now()));
    }

}
