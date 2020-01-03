package com.albert.okrouter.core;

import com.albert.okrouter.RouterConstant;
import com.albert.okrouter.annotation.model.UniqueKeyTreeMap;
import com.albert.okrouter.exception.OkRouterException;
import com.albert.okrouter.interceptor.InterceptorCallback;
import com.albert.okrouter.interceptor.RealInterceptorChain;
import com.albert.okrouter.interceptor.RouterInterceptor;
import com.albert.okrouter.provide.IBaseAction;
import com.albert.okrouter.provide.ProviderService;
import com.albert.okrouter.thread.CancelableCountDownLatch;
import com.albert.okrouter.utils.Rlog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-05.
 *      Desc         :
 * </pre>
 */
public class RouterDispatcher implements RouterInterceptor {

    private static final String TAG = RouterDispatcher.class.getSimpleName();
    private static ThreadPoolExecutor executor;
    /**
     * 判断是否初始化完成
     */
    private static boolean interceptorHasInit;
    private static final Object interceptorInitLock = new Object();


    public synchronized static void init(final Class routerCacheClazz, ThreadPoolExecutor tpe) {
        executor = tpe;
        initCache(routerCacheClazz);
    }

    /**
     * 获取&初始化拦截器容器
     *
     * @param routerCacheClazz
     * @throws Exception
     */
    private static void initCache(final Class routerCacheClazz) {
        // 初始化
        try {
            final UniqueKeyTreeMap<Integer, Class<? extends RouterInterceptor>> tInterceptorsMap = (UniqueKeyTreeMap<Integer, Class<? extends RouterInterceptor>>) routerCacheClazz.getMethod(RouterConstant.GET_INTERCEPTORS_CACHE_METHOD).invoke(routerCacheClazz);
            final HashMap<String, Class<? extends IBaseAction>> tActions = (HashMap<String, Class<? extends IBaseAction>>) routerCacheClazz.getMethod(RouterConstant.GET_ACTIONS_CACHE_METHOD).invoke(routerCacheClazz);

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // 有序map转换list
                    if (tInterceptorsMap != null && tInterceptorsMap.size() > 0) {
                        for (Map.Entry<Integer, Class<? extends RouterInterceptor>> entry : tInterceptorsMap.entrySet()) {
                            Class<? extends RouterInterceptor> interceptorClass = entry.getValue();
                            try {
                                RouterInterceptor routerInterceptor = interceptorClass.getConstructor().newInstance();
                                Router.mInterceptors.add(routerInterceptor);
                            } catch (Exception ex) {
                                throw new OkRouterException(TAG + " init mInterceptors error! name = [" + interceptorClass.getName() + "], reason = [" + ex.getMessage() + "]");
                            }
                        }
                    }

                    // 初始化转换action
                    if (tActions != null && tActions.size() > 0) {
                        for (Map.Entry<String, Class<? extends IBaseAction>> entry : tActions.entrySet()) {
                            Class<? extends IBaseAction> actionClass = entry.getValue();
                            try {
                                IBaseAction action = actionClass.getConstructor().newInstance();
                                ProviderService.mActionInstances.put(entry.getKey(), action);
                            } catch (Exception ex) {
                                throw new OkRouterException(TAG + " init mActionInstances error! name = [" + actionClass.getName() + "], reason = [" + ex.getMessage() + "]");
                            }
                        }
                    }

                    // 线程同步相关
                    interceptorHasInit = true;
                    Rlog.e(TAG, "Router interceptors init over.");
                    synchronized (interceptorInitLock) {
                        interceptorInitLock.notifyAll();
                    }
                    Rlog.e("Router_time:", System.currentTimeMillis() + "");
                }
            });

        } catch (Exception e) {
            throw new OkRouterException(TAG + " init initCache error!, reason = [" + e.toString() + "]  or UniqueKeyTreeMap More than one interceptors use same priority");
        }
    }

    @Override
    public void intercept(final RouteEntity routeEntity, final InterceptorCallback callback) {
        if (executor == null || Router.mInterceptors == null || Router.mInterceptors.size() <= 0) {
            callback.onInterrupt(new OkRouterException("The interceptors cache is null or size == 0"));
            return;
        }

        // 异步执行责任链模式，根据优先级调用拦截器
        checkInterceptorsInitStatus();
        if (!interceptorHasInit) {
            callback.onInterrupt(new OkRouterException("Interceptors initialization takes too much time."));
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                CancelableCountDownLatch interceptorCounter = new CancelableCountDownLatch(Router.mInterceptors.size());
                try {
                    Rlog.e("Router_execute", System.currentTimeMillis() + "");
                    RealInterceptorChain chain = new RealInterceptorChain(interceptorCounter, Router.mInterceptors);
                    chain.proceed(routeEntity);
                    // 如果在指定的时间内达到interceptors.size()的数量，则程序继续向下运行，否则如果出现超时，则抛出TimeoutException/OkRouterException异常
                    // 如果你们拦截器没有回调callback，也会超时。请及时回调callback，有肯能造成死循环
                    interceptorCounter.await(routeEntity.getOutTime(), TimeUnit.MILLISECONDS);// 1000毫秒内没有处理完，就超时了
                    Rlog.e("Router_execute", System.currentTimeMillis() + "");
                    if (interceptorCounter.getCount() > 0) {    // Cancel the navigation this time, if it hasn't return anythings.
                        callback.onInterrupt(new OkRouterException("The interceptor processing timed out. or interceptor not call InterceptorCallback."));
                    } else {
                        callback.onContinue(routeEntity);
                    }
                } catch (Exception e) {
                    callback.onInterrupt(e);
                }
            }
        });

    }

    /**
     * 检查初始化是否完成，没有完成，拦截器不能工作，等待10s
     */
    private static void checkInterceptorsInitStatus() {
        synchronized (interceptorInitLock) {
            while (!interceptorHasInit) {
                try {
                    interceptorInitLock.wait(10 * 1000);
                } catch (InterruptedException e) {
                    throw new OkRouterException(TAG + " init interceptors cost too much time error! reason = [" + e.getMessage() + "]");
                }
            }
        }
    }

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
