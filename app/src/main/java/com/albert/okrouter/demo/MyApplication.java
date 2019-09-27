package com.albert.okrouter.demo;

import android.app.Application;

import com.albert.okrouter.OkRouterConfig;
import com.albert.okrouter.core.RouteEntity;
import com.albert.okrouter.interceptor.InterceptorCallback;
import com.albert.okrouter.interceptor.RouterInterceptor;
import com.squareup.leakcanary.LeakCanary;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-07-29.
 *      Desc         :
 * </pre>
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }

        OkRouterConfig.getInstance()
                .init(this, false)
                .addInterceptor(new RouterInterceptor() {
                    @Override
                    public void intercept(RouteEntity routeEntity, InterceptorCallback callback) {
                        routeEntity.putString("age", "1");
                        callback.onContinue(routeEntity);
                    }
                });
    }

}
