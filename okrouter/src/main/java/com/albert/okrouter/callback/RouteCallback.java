package com.albert.okrouter.callback;

import com.albert.okrouter.core.RouteEntity;
import com.albert.okrouter.exception.OkRouterException;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2020.
 *      Author       : jiaoya.
 *      Created Time : 2020-01-03.
 *      Desc         :
 * </pre>
 */
public interface RouteCallback {

    /**
     * Callback after navigation.
     *
     * @param routeEntity meta
     */
    void onArrival(RouteEntity routeEntity);

    /**
     * Callback on interrupt.
     *
     * @param e
     */
    void onInterrupt(OkRouterException e);

}
