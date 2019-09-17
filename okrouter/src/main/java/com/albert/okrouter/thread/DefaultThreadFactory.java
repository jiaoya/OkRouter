package com.albert.okrouter.thread;

import android.support.annotation.NonNull;

import com.albert.okrouter.utils.Rlog;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-06.
 *      Desc         : 线程工厂
 * </pre>
 */
public class DefaultThreadFactory implements ThreadFactory {
    private static final String TAG = DefaultThreadFactory.class.getSimpleName();

    private static final AtomicInteger poolNumber = new AtomicInteger(1);// AtomicInteger是一个提供原子操作的Integer类，通过线程安全的方式操作加减
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;

    public DefaultThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "OkRouter task pool No." + poolNumber.getAndIncrement() + ", thread No.";
    }

    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        String threadName = namePrefix + threadNumber.getAndIncrement();

        Thread thread = new Thread(group, runnable, threadName, 0);
        if (thread.isDaemon()) {   //设为非后台线程
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) { //优先级为normal
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        // 捕获多线程处理中的异常
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Rlog.e(TAG, "Running task appeared exception! Thread [" + thread.getName() + "], because [" + ex.getMessage() + "]");
            }
        });
        return thread;
    }
}