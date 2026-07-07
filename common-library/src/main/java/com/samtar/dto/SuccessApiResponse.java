package com.samtar.dto;

import java.time.LocalDateTime;

public record SuccessApiResponse<T>(
        String message,
        T data,
        LocalDateTime timestamp
) {

    public SuccessApiResponse<T> successApiResponse(String message,
                                                    T data) {
        return new SuccessApiResponse<>(message, data, LocalDateTime.now());
    }
}
