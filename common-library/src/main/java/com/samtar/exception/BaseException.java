package com.samtar.exception;

public class BaseException extends RuntimeException{
    Object statusCode;
    public BaseException(String message,Object statusCode) {
        super(message);
        this.statusCode = statusCode;
    }


    public Object getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Object statusCode) {
        this.statusCode = statusCode;
    }
}
