package com.kkori.exception;

import lombok.Getter;

@Getter
public class CustomRuntimeException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public CustomRuntimeException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public int getCode() {
        return exceptionCode.getCode();
    }

    public String getExceptionMessage() {
        return exceptionCode.getMessage();
    }
}