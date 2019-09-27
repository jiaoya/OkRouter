package com.albert.okrouter.demo;


import com.albert.okrouter.annotation.InterceptPoint;
import com.albert.okrouter.core.RouteEntity;
import com.albert.okrouter.interceptor.InterceptorCallback;
import com.albert.okrouter.interceptor.RouterInterceptor;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-07.
 *      Desc         : 拦截单个地址
 * </pre>
 */
@InterceptPoint(adress = "/app/Main3Activity")
public class InterceptorPointTest implements RouterInterceptor {

    @Override
    public void intercept(RouteEntity routeEntity, InterceptorCallback callback) {
        routeEntity.putBoolean("point", false);
        callback.onContinue(routeEntity);
    }

}
