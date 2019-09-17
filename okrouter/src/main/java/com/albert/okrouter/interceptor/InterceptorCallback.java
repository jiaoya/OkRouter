package com.albert.okrouter.interceptor;

import com.albert.okrouter.core.RouteEntity;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-05.
 *      Desc         :
 * </pre>
 */
public interface InterceptorCallback {

    /**
     * Continue process
     *
     * @param routeEntity route meta
     */
    void onContinue(RouteEntity routeEntity);

    /**
     * Interrupt process, pipeline will be destroy when this method called.
     *
     * @param exception Reson of interrupt.
     */
    void onInterrupt(Throwable exception);
}
