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
public abstract class NavCallback implements RouteCallback {

    @Override
    public void onArrival(RouteEntity routeEntity) {

    }

    @Override
    public void onInterrupt(OkRouterException e) {

    }
}
