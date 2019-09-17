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
public interface RouterInterceptor {

    void intercept(RouteEntity routeEntity, InterceptorCallback callback);

    interface Chain {
        RouteEntity proceed(RouteEntity routeEntity);
    }

}
