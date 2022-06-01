package com.tw.heima.exception;

public enum ExceptionType {
    UNHANDLED_EXCEPTION("000000", "unhandled exception"),
    INPUT_PARAM_INVALID("000001", "input param invalid");

    private String code;
    private String msg;

    ExceptionType(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
