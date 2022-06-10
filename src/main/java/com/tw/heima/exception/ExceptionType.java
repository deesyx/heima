package com.tw.heima.exception;

public enum ExceptionType {
    UNHANDLED_EXCEPTION("000000", "unhandled exception"),
    INPUT_PARAM_INVALID("000001", "input param invalid"),
    DATA_NOT_FOUND("000002", "data not found"),
    RETRY_LATTER("000003", "please retry later"),
    CONTACT_IT("000004", "please contact with IT");

    private final String code;
    private final String msg;

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
