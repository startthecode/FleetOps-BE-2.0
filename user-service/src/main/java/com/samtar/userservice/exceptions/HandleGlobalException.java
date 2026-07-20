package com.samtar.userservice.exceptions;


import com.samtar.dto.ExceptionApiResponse;
import com.samtar.exception.BaseException;
import com.samtar.userservice.constants.MessageConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class HandleGlobalException {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(BaseException err){
        return ResponseEntity.status((HttpStatusCode) err.getStatusCode()).body(new ExceptionApiResponse<>(null, err.getMessage(), LocalDateTime.now()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionApiResponse<String>> handleGlobalError(Exception err){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionApiResponse<>(null, MessageConstant.FAIL_TO_EXECUTE, LocalDateTime.now()));
    }

}
