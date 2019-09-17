package com.albert.okrouter.provide;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-21.
 *      Desc         :
 * </pre>
 */
public interface ActionCallback<T> {

    void result(ActionResult<T> result);

    void error(Exception e);
}
