package com.albert.okrouter;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Keep;

import com.albert.okrouter.core.OkRouter;
import com.albert.okrouter.interceptor.RouterInterceptor;
import com.albert.okrouter.utils.ProcessUtil;
import com.albert.okrouter.utils.Rlog;


/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-01.
 *      Desc         : 初始化路由
 * </pre>
 */
public class OkRouterConfig {


    private static OkRouterConfig instance;

    public OkRouterConfig() {

    }

    public static OkRouterConfig getInstance() {
        if (instance == null) {
            synchronized (OkRouterConfig.class) {
                if (instance == null) {
                    instance = new OkRouterConfig();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public OkRouterConfig init(Application context) {
        return init(context, false);
    }

    /**
     * 初始化
     *
     * @param context
     * @param isOpenLog 打开日志
     * @return
     */
    public OkRouterConfig init(Application context, boolean isOpenLog) {
        return init(context, isOpenLog, false);
    }

    /**
     * 初始化
     *
     * @param context
     * @param isOpenLog 打开日志
     * @param isAuto    是否要自动获取当前activity,如果不设置，当不传入当前Activity时，会使用Application进行跳转
     * @return
     */
    public OkRouterConfig init(Application context, boolean isOpenLog, boolean isAuto) {
        Rlog.setShowLog(isOpenLog);
        ProcessUtil.init(context);
        OkRouter.init(context);
        if (isAuto) {
            OkRouter.getApplicationContext().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
        return this;
    }

    /**
     * 添加全局拦截器,优先级最高，始终添加在头部
     *
     * @return
     */
    @Keep
    public OkRouterConfig addInterceptor(RouterInterceptor interceptor) {
        OkRouter.setInterceptors(interceptor);
        return this;
    }

    Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            OkRouter.getInstance().setCurrentActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (OkRouter.getInstance().getCurrentActivity() == activity) {
                OkRouter.getInstance().setCurrentActivity(null);
            }
        }
    };
}
