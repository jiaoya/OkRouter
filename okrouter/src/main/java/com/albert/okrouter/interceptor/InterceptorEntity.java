package com.albert.okrouter.interceptor;

import androidx.annotation.NonNull;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-08.
 *      Desc         :
 * </pre>
 */
@Deprecated
public class InterceptorEntity implements Comparable<InterceptorEntity> {

    public InterceptorEntity(Integer priority, Class<? extends RouterInterceptor> cls) {
        this.priority = priority;
        this.interceptor = cls;
    }

    public Integer priority;
    public Class interceptor;


    @Override
    public int compareTo(@NonNull InterceptorEntity o) {
        return this.priority - o.priority;
    }
}
