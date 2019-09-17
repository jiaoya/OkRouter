package com.albert.okrouter.annotation.model;

import java.util.TreeMap;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-12.
 *      Desc         : 有序的key-value集合,保证key or vlue 唯一
 * </pre>
 */
public class UniqueKeyTreeMap<K, V> extends TreeMap<K, V> {

    @Override
    public V put(K key, V value) {
        if (containsKey(key) || containsValue(value)) {
            throw new RuntimeException(String.format("More than one interceptors use same priority [%s]", key));
        } else {
            return super.put(key, value);
        }
    }
}
