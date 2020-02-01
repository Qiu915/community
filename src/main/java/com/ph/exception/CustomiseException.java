package com.ph.exception;

public class CustomiseException extends RuntimeException {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public CustomiseException(CustomiseErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }
}
