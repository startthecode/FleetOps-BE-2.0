package com.samtar.exception;

import java.util.Map;

public class ValidationException extends BaseException {
    private final Map<String, String> errors;
    public ValidationException(String message,Object statusCode,Map<String, String> errors) {
        super(message,statusCode);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
