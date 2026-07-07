package com.samtar.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ExceptionApiResponse<T> (
        T error,
        String message,
        LocalDateTime timestamp
){
  public static <T> ExceptionApiResponse<T> of(T error, String message) {
    return new ExceptionApiResponse<>(error, message, LocalDateTime.now());
  }
}