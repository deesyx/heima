package com.tw.heima.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BadRequestException extends RuntimeException {
    private final ExceptionType type;
    private final String detail;

    public BadRequestException(ExceptionType type, String detail) {
        super(detail);
        this.type = type;
        this.detail = detail;
    }
}
