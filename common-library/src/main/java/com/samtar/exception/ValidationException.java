package com.samtar.exception;

import java.util.Map;

public class ValidationException extends BaseException {
    private final Map<String, String> errors;
    public ValidationException(String message,Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}
