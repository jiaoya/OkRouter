package com.albert.okrouter.core;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.albert.okrouter.RouterConstant;
import com.albert.okrouter.exception.OkRouterException;
import com.albert.okrouter.interceptor.InterceptorCallback;
import com.albert.okrouter.interceptor.RouterInterceptor;
import com.albert.okrouter.provide.ActionCallback;
import com.albert.okrouter.provide.ActionResult;
import com.albert.okrouter.provide.IBaseAction;
import com.albert.okrouter.provide.IProviderAidlInterface;
import com.albert.okrouter.provide.ProviderService;
import com.albert.okrouter.thread.DefaultPoolExecutor;
import com.albert.okrouter.thread.RouterScheduler;
import com.albert.okrouter.utils.ProcessUtil;
import com.albert.okrouter.utils.Rlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.albert.okrouter.RouterConstant.BASE_SCHME;
import static com.albert.okrouter.RouterConstant.CLASS_HOLD_ADDRESS_FOR_URI;
import static com.albert.okrouter.RouterConstant.GET_ADRESS_INTERCEPTORS_CACHE_METHOD;
import static com.albert.okrouter.RouterConstant.GET_ANNOTATED_CLASSES_METHOD;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-06-03.
 *      Desc         : Router核心规则逻辑的处理
 * </pre>
 */
class Router {

    private static final String TAG = Router.class.getSimpleName();
    private volatile static Router instance;
    private Context mCurrentActivity;
    private static Application mApplicationContext;
    private static Handler mHandler;
    private RouterDispatcher dispatcher;

    public static HashMap<String, Class> localRouterAddress;                                        // 跳转地址
    public static HashMap<String, Class<? extends RouterInterceptor>> mInterceptorPoints;           // 拦截单个地址
    public static List<RouterInterceptor> mInterceptors = new ArrayList<>();                        // 全局拦截，根据优先级

    public static HashMap<String, Class<? extends ProviderService>> mServices = new HashMap<>();    // 保存进程(服务)端
    private HashMap<String, IProviderAidlInterface> binders = new HashMap<>();                      // 保存已经连接的binder对象
    private HashMap<String, ServiceConnection> mServiceConnections = new HashMap<>();               // 保存链接，用来关闭链接

    public static void init(Application context) {
        Rlog.e("Router_init_start_time:", System.currentTimeMillis() + "");
        mApplicationContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        Class routerCacheClazz = null;
        try {
            routerCacheClazz = Class.forName(CLASS_HOLD_ADDRESS_FOR_URI);
        } catch (ClassNotFoundException e) {
            throw new OkRouterException(TAG + " init  error!, reason = [" + CLASS_HOLD_ADDRESS_FOR_URI + " NotFound ]");
        }
        initLocalRouterCache(routerCacheClazz);
        RouterDispatcher.init(routerCacheClazz, DefaultPoolExecutor.getInstance());
        Rlog.e("Router_init_end_time:", System.currentTimeMillis() + "");
    }

    /**
     * 获取本地路由信息
     *
     * @return
     */
    private static void initLocalRouterCache(Class routerCacheClazz) {
        try {
            if (localRouterAddress == null || localRouterAddress.size() == 0) {
                localRouterAddress = (HashMap<String, Class>) (routerCacheClazz.getMethod(GET_ANNOTATED_CLASSES_METHOD).invoke(routerCacheClazz));
            }
            if (mInterceptorPoints == null || mInterceptorPoints.size() == 0) {
                mInterceptorPoints = (HashMap<String, Class<? extends RouterInterceptor>>) routerCacheClazz.getMethod(GET_ADRESS_INTERCEPTORS_CACHE_METHOD).invoke(routerCacheClazz);
            }
            if (mServices == null || mServices.size() == 0) {
                mServices = (HashMap<String, Class<? extends ProviderService>>) routerCacheClazz.getMethod(RouterConstant.GET_PROVIDERS_CACHE_METHOD).invoke(routerCacheClazz);
            }
        } catch (Exception e) {
            throw new OkRouterException(TAG + " init  error!, reason = [" + e.toString() + "]");
        }
    }

    public static Application getApplication() {
        return mApplicationContext;
    }

    public static Router getInstance() {
        if (instance == null) {
            synchronized (Router.class) {
                if (instance == null) {
                    instance = new Router();
                }
            }
        }
        return instance;
    }

    /**
     * 添加拦截器
     *
     * @param interceptor
     */
    public static void setInterceptors(RouterInterceptor interceptor) {
        if (!mInterceptors.contains(interceptor)) {
            mInterceptors.add(0, interceptor);
        }
    }

    /**
     * 设置当前Activity
     *
     * @param currentActivity
     * @return
     */
    public Router setCurrentActivity(Context currentActivity) {
        this.mCurrentActivity = currentActivity;
        return this;
    }

    public Context getCurrentActivity() {
        return mCurrentActivity;
    }


    /**
     * 构建路由数据承载体，如果地址没有scheme,必须以 ‘/’ 开头，并且不能超过两个 ‘//’
     *
     * @return
     */
    public RouteEntity build(String adress) {
        if (TextUtils.isEmpty(adress)) {
            throw new RuntimeException("Router destination class is null");
        }
        // 如果被编码先解码
        adress = Uri.decode(adress);
        Uri uri = Uri.parse(adress);

        // 如果没有schme,要添加默认的
        if (TextUtils.isEmpty(uri.getScheme())) {
            if (!adress.startsWith("/") || adress.substring(0, 2).equals("//")) {
                throw new OkRouterException("the path must be start with '/' and not more than 2 '/'!");
            }
            // 转换设置成 uri，没有scheme，添加默认scheme
            uri = Uri.parse(BASE_SCHME + adress);
            RouteEntity routeEntity = new RouteEntity(adress, uri);
            return build(routeEntity);
        } else {
            return build(uri);
        }
    }

    public RouteEntity build(Uri uri) {

        if (TextUtils.isEmpty(uri.getScheme())) {
            throw new OkRouterException("uri not contain scheme! or UrlEncode!");
        }

        String adress = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        RouteEntity routeEntity = new RouteEntity(adress, uri);
        return build(routeEntity);
    }

    private RouteEntity build(RouteEntity routeEntity) {
        // 解析参数
        analysisUriParams(routeEntity);
        // 获取目标类
        //setDestination(routeEntity);
        dispatcher = new RouterDispatcher();
        return routeEntity;
    }

    /**
     * 获取目标类，并设置到容器里
     *
     * @param routeEntity
     */
    private void setDestination(RouteEntity routeEntity) {
        // 获取设置目标类
        Class destination = localRouterAddress.get(routeEntity.getAdress());
        if (destination != null) {
            routeEntity.setDestination(destination);
        }
    }

    /**
     * 解析Uri里的参数
     */
    private void analysisUriParams(RouteEntity routeEntity) {
        Set<String> names = routeEntity.getUri().getQueryParameterNames();
        if (names.size() <= 0) {
            return;
        }
        for (String name : names) {
            String value = routeEntity.getUri().getQueryParameter(name);
            if (!TextUtils.isEmpty(value)) {
                routeEntity.putString(name, value);
            }
        }
    }

    /**
     * 开始处理全局拦截器
     *
     * @param routeEntity
     * @param currentContext
     * @param requestCode
     * @return
     */
    public Object navigation(final RouteEntity routeEntity, final Context currentContext, final int requestCode) {
        // 拦截
        dispatcher.intercept(routeEntity, new InterceptorCallback() {
            @Override
            public void onContinue(RouteEntity routeEntity) {
                if (routeEntity == null) {
                    throw new OkRouterException(TAG + " Interceptor get RouteEntity is null");
                } else {
                    navigationInterceptorPint(routeEntity, currentContext, requestCode);
                }
            }

            @Override
            public void onInterrupt(Throwable exception) {
                Rlog.e(TAG, exception.toString());
                navigationInterceptorPint(routeEntity, currentContext, requestCode);
            }
        });
        return null;
    }

    /**
     * 处理局部烂机器
     *
     * @param routeEntity
     * @param currentContext
     * @param requestCode
     * @return
     */
    public Object navigationInterceptorPint(final RouteEntity routeEntity, final Context currentContext, final int requestCode) {

        if (mInterceptorPoints != null
                && mInterceptorPoints.size() > 0
                && mInterceptorPoints.containsKey(routeEntity.getAdress())) {
            try {
                RouterInterceptor interceptorPoint = mInterceptorPoints.get(routeEntity.getAdress()).getConstructor().newInstance();
                interceptorPoint.intercept(routeEntity, new InterceptorCallback() {
                    @Override
                    public void onContinue(RouteEntity routeEntity) {
                        if (routeEntity == null) {
                            throw new OkRouterException(TAG + " Interceptor get RouteEntity is null");
                        } else {
                            navigationDestinationClass(currentContext, routeEntity, requestCode);
                        }
                    }

                    @Override
                    public void onInterrupt(Throwable exception) {
                        Rlog.e(TAG, exception.toString());
                        navigationDestinationClass(currentContext, routeEntity, requestCode);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                navigationDestinationClass(currentContext, routeEntity, requestCode);
            }
        } else {
            navigationDestinationClass(currentContext, routeEntity, requestCode);
        }
        return null;
    }

    /**
     * 跳转/获取 目标类过滤相关处理
     *
     * @param routeEntity
     * @return
     */
    private Object navigationDestinationClass(final Context currentContext, final RouteEntity routeEntity, final int requestCode) {

        // 获取类
        setDestination(routeEntity);

        if (routeEntity.getDestination() == null) {
            Toast.makeText(mApplicationContext, "okRouter destination class is null", Toast.LENGTH_SHORT).show();
            return null;
        }

        // 判断目标类的类型
        if (Activity.class.isAssignableFrom(routeEntity.getDestination())) {
            // 如果是Activity
            return navigationActivity(currentContext, routeEntity, requestCode);
        } else if (Fragment.class.isAssignableFrom(routeEntity.getDestination())
                || android.app.Fragment.class.isAssignableFrom(routeEntity.getDestination())) {
            // 如果是Fragment
            return navigationFragmegt(routeEntity);
        } else {
            throw new OkRouterException(TAG + "  destination class is not Activity or Fragment");
        }
        // return null;
    }

    /**
     * 跳转到Activity
     *
     * @param context
     * @param routeEntity
     * @param requestCode
     * @return
     */
    private Object navigationActivity(final Context context, final RouteEntity routeEntity, final int requestCode) {
        final Context currentContext;
        if (context == null) {
            if (mCurrentActivity == null) {
                currentContext = mApplicationContext;
                if (-1 == routeEntity.getFlags()) {
                    routeEntity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            } else {
                currentContext = mCurrentActivity;
            }
        } else {
            currentContext = context;
        }

        final Intent intent = new Intent(currentContext, routeEntity.getDestination());
        // 判断是否需要添加Flags
        if (-1 != routeEntity.getFlags()) {
            intent.setFlags(routeEntity.getFlags());
        }
        // 添加参数
        intent.putExtras(routeEntity.getExtras());

        runInMainThread(new Runnable() {
            @Override
            public void run() {
                startActivity(currentContext, intent, routeEntity, requestCode);
            }
        });
        return null;
    }

    /**
     * 启动Activity
     *
     * @param currentContext
     * @param intent
     * @param routeEntity
     * @param requestCode
     */
    private void startActivity(final Context currentContext, final Intent intent, final RouteEntity routeEntity, final int requestCode) {
        if (0 >= requestCode) {
            currentContext.startActivity(intent, routeEntity.getOptions());
        } else {
            if (currentContext instanceof Activity) {
                // ApplicationContext，无法startActivityForResult
                ((Activity) currentContext).startActivityForResult(intent, requestCode, routeEntity.getExtras());
            } else {
                Toast.makeText(mApplicationContext, "Application Can't call startActivityForResult，Must use [navigation(activity, ...)] ", Toast.LENGTH_SHORT).show();
            }
        }

        // 添加跳转动画
        if (-1 != routeEntity.getEnterAnim() && -1 != routeEntity.getExitAnim() && currentContext instanceof Activity) {
            ((Activity) currentContext).overridePendingTransition(routeEntity.getEnterAnim(), routeEntity.getExitAnim());
        }
        mCurrentActivity = null;
    }

    /**
     * Be sure execute in main thread.
     *
     * @param runnable code
     */
    private void runInMainThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * Be sure execute in main thread.
     *
     * @param runnable
     * @param delayMillis 延迟x秒发送
     */
    private void runInMainThread(Runnable runnable, long delayMillis) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            mHandler.postDelayed(runnable, delayMillis);
        } else {
            runnable.run();
        }
    }

    /**
     * 返回fragment
     *
     * @param routeEntity
     * @return
     */
    private Object navigationFragmegt(final RouteEntity routeEntity) {
        Class fragmentMeta = routeEntity.getDestination();
        try {
            Object instance = fragmentMeta.getConstructor().newInstance();
            if (instance instanceof android.app.Fragment) {
                ((android.app.Fragment) instance).setArguments(routeEntity.getExtras());
            } else if (instance instanceof Fragment) {
                ((Fragment) instance).setArguments(routeEntity.getExtras());
            }
            return instance;
        } catch (Exception ex) {
            Rlog.e(TAG, "Fetch fragment instance error, " + ex.getLocalizedMessage());
            return null;
        }
    }


    /*-------------------------进程间通信-----------------------*/

    public ActionEntity bind(@NonNull String actionName) {
        return bind(null, actionName);
    }

    public ActionEntity bind(String processName, @NonNull String actionName) {
        if (TextUtils.isEmpty(actionName)) {
            throw new RuntimeException("Router actionName  is null");
        }
        ActionEntity entity = new ActionEntity(processName, actionName);
        entity.setContext(mCurrentActivity);
        return entity;
    }

    /**
     * 返回接口
     *
     * @param actionEntity
     * @return
     */
    public IBaseAction connect(final ActionEntity actionEntity) {
        if (!ProcessUtil.isMainProcess()) {
            throw new OkRouterException("This method cannot be used when across processes, Only in the main process");
        } else if (!TextUtils.isEmpty(actionEntity.getProcessName()) && actionEntity.getProcessName().equals(ProcessUtil.getCurrentProcessName())) {
            throw new OkRouterException("This method cannot be used when across processes, Only in the main process");
        }
        return ProviderService.mActionInstances.get(actionEntity.getActionUri().toString());
    }

    /**
     * 为了防止主线程被挂起，链接获取action在子线程里，回调的线程具体看设置选项
     *
     * @param actionEntity
     * @param callback
     */
    public void connectThread(final ActionEntity actionEntity, final ActionCallback callback) {
        // 前线程为主线程则开启新子线程处理
        if (Looper.getMainLooper() != Looper.myLooper()) {
            actionEntity.setMainThread(false);
            connect(actionEntity, callback);
        } else {
            actionEntity.setMainThread(true);
            RouterDispatcher.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    connect(actionEntity, callback);
                }
            });
        }
    }

    /**
     * 链接进程判断
     *
     * @param actionEntity
     * @param callback
     */
    private void connect(final ActionEntity actionEntity, final ActionCallback callback) {
        if (actionEntity == null) {
            return;
        }
        if (ProcessUtil.isMainProcess()) {
            // 主进程内
            if (TextUtils.isEmpty(actionEntity.getProcessName()) || actionEntity.getProcessName().equals(ProcessUtil.getCurrentProcessName())) {
                // processName空可以直接 获取action，,不用写进程名，不用binder通讯 || 填写的进程名和当前进程相同，也不进行binder通讯
                connectAction(actionEntity, callback);
            } else {
                // 进程通讯
                connectProcess(actionEntity, callback);
            }
        } else if (!ProcessUtil.isMainProcess()) {
            // 子进程内
            if (TextUtils.isEmpty(actionEntity.getProcessName())) {
                throw new OkRouterException("Child process, must write other process name");
            }
            if (actionEntity.getProcessName().equals(ProcessUtil.getCurrentProcessName())) {
                // 填写的进程名和当前进程相同，也不进行binder通讯
                connectAction(actionEntity, callback);
            } else {
                // 进程通讯
                connectProcess(actionEntity, callback);
            }
        }
    }

    /**
     * 当前进程直接获取接口数据
     *
     * @param actionEntity
     * @param callback
     */
    private void connectAction(final ActionEntity actionEntity, final ActionCallback callback) {
        try {
            IBaseAction iBaseAction = ProviderService.mActionInstances.get(actionEntity.getActionUri().toString());
            if (iBaseAction == null) {
                throw new OkRouterException(" Please check whether processName or actionAdress is null");
            }
            ActionResult result = iBaseAction.invoke(actionEntity.getContext(), actionEntity.getData());
            actionCallbackDeal(actionEntity, result, callback);
        } catch (Exception e) {
            callback.error(e);
        }
    }

    /**
     * 进程通讯获取接口数据
     *
     * @param actionEntity
     * @param callback
     */
    private void connectProcess(final ActionEntity actionEntity, final ActionCallback callback) {

        if (!mServices.containsKey(actionEntity.getProcessName())) {
            callback.error(new OkRouterException("Could not find processName,please check the processName of the settings"));
            return;
        }
        Intent intent = new Intent(mApplicationContext, mServices.get(actionEntity.getProcessName()));
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IProviderAidlInterface iBinder;
                if (binders.get(actionEntity.getProcessName()) == null) {
                    iBinder = IProviderAidlInterface.Stub.asInterface(service);
                    binders.put(actionEntity.getProcessName(), iBinder);
                } else {
                    iBinder = binders.get(actionEntity.getProcessName());
                }
                Rlog.e(TAG, "onServiceConnected" + actionEntity.toString() + "----" + iBinder.toString());
                try {
                    // 获取回调
                    ActionResult result = iBinder.getAction(actionEntity.getActionUri().toString(), actionEntity.getData());
                    actionCallbackDeal(actionEntity, result, callback);
                } catch (Exception e) {
                    callback.error(e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                binders.remove(actionEntity.getProcessName());
                mServiceConnections.remove(actionEntity.getProcessName());
                Rlog.e(TAG, "onServiceDisconnected");
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Rlog.e(TAG, "onBindingDied");
            }
        };
        boolean isConnect = mApplicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        if (isConnect) {
            mServiceConnections.put(actionEntity.getProcessName(), connection);
        } else {
            callback.error(new OkRouterException("bindService process is failed"));
        }
    }

    /**
     * 处理回调
     *
     * @param actionEntity
     * @param result
     * @param callback
     */
    private void actionCallbackDeal(final ActionEntity actionEntity, final ActionResult result, final ActionCallback callback) {
        try {
            if (actionEntity.getScheduler() == RouterScheduler.NORMAL) {
                if (actionEntity.isMainThread()) {
                    runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.result(result);
                        }
                    });
                } else {
                    callback.result(result);
                }
            } else if (actionEntity.getScheduler() == RouterScheduler.MAIN) {
                runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.result(result);
                    }
                });
            } else {
                callback.result(result);
            }
        } catch (Exception e) {
            callback.error(e);
        }
    }

    /**
     * 本进程断开所链接某一个进程服务，但不杀死进程
     *
     * @param processName
     */
    public boolean disconnect(String processName) {
        if (!TextUtils.isEmpty(processName) && mServiceConnections.containsKey(processName)) {
            mApplicationContext.unbindService(mServiceConnections.get(processName));
            binders.remove(processName);
            mServiceConnections.remove(processName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 断开本进程所有链接的进程服务
     */
    public boolean disconnectAll() {
        if (mServiceConnections.size() > 0) {
            Iterator<String> keyList = mServiceConnections.keySet().iterator();
            while (keyList.hasNext()) {
                String service = keyList.next();
                mApplicationContext.unbindService(mServiceConnections.get(service));
            }
            binders.clear();
            mServiceConnections.clear();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 关闭进程
     *
     * @param processName 进程名
     * @return
     */
    public boolean close(String processName) {
        if (!TextUtils.isEmpty(processName)) {
            if (processName.equals(ProcessUtil.getMainProcessName())) {
                // 不允许调用此方法杀死主进程
                return false;
            }
            if (processName.equals(ProcessUtil.getCurrentProcessName())) {
                // 当前进程
                disconnectAll();
                mApplicationContext = null;
                RouterDispatcher.getExecutor().shutdown();
                closeSelf();
                return true;
            } else {
                // 其他进程
                if (binders.containsKey(processName)) {
                    IProviderAidlInterface iBinder = binders.get(processName);
                    try {
                        iBinder.close();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    disconnect(processName);
                    return true;
                } else {
                    // 未找到
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 关闭当前进程
     */
    public void closeSelf() {
        if (!ProcessUtil.isMainProcess()) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }


}
