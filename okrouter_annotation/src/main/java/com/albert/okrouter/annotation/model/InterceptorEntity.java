package com.albert.okrouter.annotation.model;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-08.
 *      Desc         :
 * </pre>
 */
public class InterceptorEntity implements Comparable<InterceptorEntity> {

    public InterceptorEntity(Integer priority, Class cls) {
        this.priority = priority;
        this.interceptor = cls;
    }

    public Integer priority;
    public Class interceptor;


    @Override
    public int compareTo(InterceptorEntity o) {
        return this.priority - o.priority;
    }
}
