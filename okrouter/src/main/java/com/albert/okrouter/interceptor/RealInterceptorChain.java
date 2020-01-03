package com.albert.okrouter.interceptor;

import com.albert.okrouter.core.RouteEntity;
import com.albert.okrouter.thread.CancelableCountDownLatch;
import com.albert.okrouter.utils.Rlog;

import java.util.List;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-06.
 *      Desc         : 责任链模式
 * </pre>
 */
public class RealInterceptorChain implements RouterInterceptor.Chain {

    private List<RouterInterceptor> interceptors;
    private CancelableCountDownLatch counter;

    public RealInterceptorChain(CancelableCountDownLatch counter, List<RouterInterceptor> interceptors) {
        this.counter = counter;
        this.interceptors = interceptors;
    }

    public RealInterceptorChain(int index, CancelableCountDownLatch counter, List<RouterInterceptor> interceptors) {
        this.counter = counter;
        this.interceptors = interceptors;
    }

    @Override
    public RouteEntity proceed(RouteEntity routeEntity) {
        proceed(0, routeEntity);
        return routeEntity;
    }

    private void proceed(final int index, final RouteEntity postcard) {
        Rlog.e("RealInterceptorChain", index + "--" + interceptors.size() + "==" + counter.getCount());
        if (counter != null && index < interceptors.size()) {
            RouterInterceptor iInterceptor = interceptors.get(index);
            // 执行回调所有全局拦截器
            iInterceptor.intercept(postcard, new InterceptorCallback() {
                @Override
                public void onContinue(RouteEntity routeEntity) {
                    counter.countDown();
                    // RealInterceptorChain（）
                    proceed(index + 1, postcard);
                }

                @Override
                public void onInterrupt(Throwable exception) {
                    counter.cancel();
                }
            });

        }
    }

}
