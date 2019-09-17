package com.albert.okrouter.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.albert.okrouter.interceptor.RouterInterceptor;


/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-07-30.
 *      Desc         : 仅供外部调用使用
 * </pre>
 */
public class OkRouter {

    private static OkRouter instance;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Application context) {
        Router.init(context);
    }

    public static Application getApplicationContext() {
        return Router.getApplication();
    }

    public static OkRouter getInstance() {
        if (instance == null) {
            synchronized (OkRouter.class) {
                if (instance == null) {
                    instance = new OkRouter();
                }
            }
        }
        return instance;
    }

    /**
     * 构建跳转体
     *
     * @param adress
     * @return
     */
    public RouteEntity build(String adress) {
        return Router.getInstance().build(adress);
    }

    /**
     * 构建跳转体
     *
     * @param uri
     * @return
     */
    public RouteEntity build(Uri uri) {
        return Router.getInstance().build(uri);
    }

    /**
     * 设置当前activity
     *
     * @param activity
     */
    public void setCurrentActivity(Activity activity) {
        Router.getInstance().setCurrentActivity(activity);
    }

    /**
     * 获取当前Activity
     *
     * @return
     */
    public Context getCurrentActivity() {
        return Router.getInstance().getCurrentActivity();
    }

    /**
     * 添加全局拦截器
     *
     * @param interceptor
     */
    public static void setInterceptors(RouterInterceptor interceptor) {
        Router.setInterceptors(interceptor);
    }

    /**
     * 绑定获取接口
     *
     * @param actionAdress
     * @return
     */
    public ActionEntity bind(String actionAdress) {
        return Router.getInstance().bind(actionAdress);
    }

    /**
     * 绑定获取接口
     *
     * @param processName
     * @param actionAdress
     * @return
     */
    public ActionEntity bind(String processName, String actionAdress) {
        return Router.getInstance().bind(processName, actionAdress);
    }

    /**
     * 本进程断开所链接某一个进程服务，但不杀死进程
     *
     * @param processName
     */
    public boolean disconnect(String processName) {
        return Router.getInstance().disconnect(processName);
    }

    /**
     * 断开本进程所有链接的进程服务，但不杀死进程
     */
    public boolean disconnectAll() {
        return Router.getInstance().disconnectAll();
    }

    /**
     * 关闭进程
     *
     * @param processName
     */
    public boolean close(String processName) {
        return Router.getInstance().close(processName);
    }

    /**
     * 关闭当前进程
     */
    public void closeSelf() {
        Router.getInstance().closeSelf();
    }

}
