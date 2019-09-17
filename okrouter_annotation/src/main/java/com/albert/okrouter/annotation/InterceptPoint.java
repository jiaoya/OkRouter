package com.albert.okrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-07.
 *      Desc         :
 * </pre>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface InterceptPoint {

    /**
     * 被拦截的地址
     *
     * @return
     */
    String adress();

    /**
     * 是否是模糊匹配，如果检测到被拦截地址中包含adress，就会被拦截
     *
     * @return
     */
    boolean isFuzzyMatching() default false;
}
