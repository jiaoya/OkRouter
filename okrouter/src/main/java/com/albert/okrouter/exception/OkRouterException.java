package com.albert.okrouter.exception;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-12.
 *      Desc         : 路由异常处理类
 * </pre>
 */
public class OkRouterException extends RuntimeException {

    private String errorMsg = "";

    public OkRouterException(Throwable e) {
        super(e);
        errorMsg = e.getMessage();
    }

    public OkRouterException(String detailMessage) {
        super(detailMessage);
        errorMsg = detailMessage;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
