package com.ph.exception;

public enum  CustomiseErrorCode {

    QUESTION_NOT_FOUND("你找的问题不存在");

    public String getMessage() {
        return message;
    }

    private String message;

    CustomiseErrorCode(String message) {
        this.message = message;
    }
}
