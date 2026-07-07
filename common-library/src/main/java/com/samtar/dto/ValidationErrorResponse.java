package com.samtar.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse<T>(
      T fields,
      String message,
      LocalDateTime timestamp
) {


    public static <T> ValidationErrorResponse<T> validationErrorResponse(T error,
                                                     String message){
        return new ValidationErrorResponse<>(error,message,LocalDateTime.now());
    }


}
