package com.albert.okrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-05-30.
 *      Desc         : 进程间通信使用，非跳转
 * </pre>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Action {
    /**
     * 进程名,如果不填写默认主进程
     *
     * @return
     */
    String processName() default "";

    /**
     * 目的
     *
     * @return
     */
    String address();
}
