package com.tw.heima.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExternalServerException extends RuntimeException {
    private final ExceptionType type;
    private final String detail;

    public ExternalServerException(ExceptionType type, String detail) {
        super(detail);
        this.type = type;
        this.detail = detail;
    }
}