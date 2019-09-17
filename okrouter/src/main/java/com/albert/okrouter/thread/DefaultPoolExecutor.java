package com.albert.okrouter.thread;

import com.albert.okrouter.utils.Rlog;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-06.
 *      Desc         : Executors 线程池
 * </pre>
 */
public class DefaultPoolExecutor extends ThreadPoolExecutor {
    private static final String TAG = DefaultPoolExecutor.class.getSimpleName();
    //    Thread args
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();    // Java虚拟机的可用的处理器数量,通常来说是硬件线程数
    private static final int INIT_THREAD_COUNT = CPU_COUNT + 1;                         // 线程数
    private static final int MAX_THREAD_COUNT = INIT_THREAD_COUNT;                      // 最大线程数
    private static final long SURPLUS_THREAD_LIFE = 30L;                                // 多余的线程

    private static volatile DefaultPoolExecutor instance;

    public static DefaultPoolExecutor getInstance() {
        if (null == instance) {
            synchronized (DefaultPoolExecutor.class) {
                if (null == instance) {
                    instance = new DefaultPoolExecutor(
                            INIT_THREAD_COUNT,
                            MAX_THREAD_COUNT,
                            SURPLUS_THREAD_LIFE,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<Runnable>(64),
                            new DefaultThreadFactory());
                }
            }
        }
        return instance;
    }

    /**
     * 线程池 初始化
     *
     * @param corePoolSize    指定了线程池中的线程数量，它的数量决定了添加的任务是开辟新的线程去执行，还是放到workQueue任务队列中去
     * @param maximumPoolSize 指定了线程池中的最大线程数量，这个参数会根据你使用的workQueue任务队列的类型，决定线程池会开辟的最大线程数量
     * @param keepAliveTime   当线程池中空闲线程数量超过corePoolSize时，多余的线程会在多长时间内被销毁
     * @param unit            keepAliveTime的单位
     * @param workQueue       任务队列，被添加到线程池中，但尚未被执行的任务；它一般分为直接提交队列、有界任务队列、无界任务队列、优先任务队列几种
     * @param threadFactory   线程工厂，用于创建线程，一般用默认即可
     */
    private DefaultPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                Rlog.e(TAG, "Task rejected, too many task!");
            }
        });
    }

    /*
     *  线程执行结束，顺便看一下有么有什么乱七八糟的异常
     *
     * @param r the runnable that has completed
     * @param t the exception that caused termination, or null if
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                ((Future<?>) r).get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
            Rlog.e(TAG, "Running task appeared exception! Thread [" + Thread.currentThread().getName() + "], because [" + t.getMessage() + "]\n" + t.getStackTrace());
        }
    }
}
