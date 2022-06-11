package com.tw.heima.controller;

import com.tw.heima.exception.BadRequestException;
import com.tw.heima.exception.DataNotFoundException;
import com.tw.heima.controller.dto.response.ExceptionResponse;
import com.tw.heima.exception.ExternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tw.heima.exception.ExceptionType.UNHANDLED_EXCEPTION;

@Slf4j
@RestControllerAdvice
public class CustomizedExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(BadRequestException e) {
        log.error("bad request exception", e);
        return ExceptionResponse.builder()
                .code(e.getType().getCode())
                .msg(e.getType().getMsg())
                .detail(e.getDetail())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @ExceptionHandler(value = DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleDataNotFoundException(DataNotFoundException e) {
        log.error("data not found exception", e);
        return ExceptionResponse.builder()
                .code(e.getType().getCode())
                .msg(e.getType().getMsg())
                .detail(e.getDetail())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @ExceptionHandler(value = ExternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleExternalServerException(ExternalServerException e) {
        log.error("external server exception", e);
        return ExceptionResponse.builder()
                .code(e.getType().getCode())
                .msg(e.getType().getMsg())
                .detail(e.getDetail())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleOtherException(Exception e) {
        log.error("unhandled exception", e);
        return ExceptionResponse.builder()
                .code(UNHANDLED_EXCEPTION.getCode())
                .msg(UNHANDLED_EXCEPTION.getMsg())
                .detail(e.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }

}
