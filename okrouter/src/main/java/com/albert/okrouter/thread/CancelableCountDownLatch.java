package com.albert.okrouter.thread;

import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-06.
 *      Desc         : 多线程控制工具类
 * </pre>
 */
public class CancelableCountDownLatch extends CountDownLatch {
    /**
     * Constructs a {@code CountDownLatch} initialized with the given count.
     *
     * @param count the number of times {@link #countDown} must be invoked
     *              before threads can pass through {@link #await}
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public CancelableCountDownLatch(int count) {
        super(count);
    }

    public void cancel() {
        while (getCount() > 0) {
            countDown();
        }
    }
}
