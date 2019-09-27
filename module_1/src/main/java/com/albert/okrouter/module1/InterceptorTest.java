package com.albert.okrouter.module1;


import com.albert.okrouter.annotation.Interceptor;
import com.albert.okrouter.core.RouteEntity;
import com.albert.okrouter.interceptor.InterceptorCallback;
import com.albert.okrouter.interceptor.RouterInterceptor;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-13.
 *      Desc         :
 * </pre>
 */
@Interceptor(priority = 1)
public class InterceptorTest implements RouterInterceptor {
    @Override
    public void intercept(RouteEntity routeEntity, InterceptorCallback callback) {
        routeEntity.putString("weight", "my weight = 70kg ");
        callback.onContinue(routeEntity);
    }
}
