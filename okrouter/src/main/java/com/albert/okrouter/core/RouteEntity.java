package com.albert.okrouter.core;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-07-30.
 *      Desc         : 路由数据容器，不处理规则逻辑
 * </pre>
 */
public class RouteEntity {

    private String adress;         // 地址/路径
    private Uri uri;
    private Class<?> destination;  // Destination 类
    private Bundle mExtras;        // Data to startActivity
    private int flags = -1;        // startActivity Flags

    private Bundle options;        // The transition animation of activity
    private int enterAnim = -1;    // 进入动画
    private int exitAnim = -1;     // 关闭动画


    public RouteEntity(String adress) {
        this(adress, null, null);
    }

    public RouteEntity(Uri uri) {
        this(null, uri, null);
    }

    public RouteEntity(String adress, Uri uri) {
        this(adress, uri, null);
    }

    private RouteEntity(String adress, Uri uri, Bundle bundle) {
        setAdress(adress);
        setUri(uri);
        this.mExtras = (null == bundle ? new Bundle() : bundle);
    }

    private RouteEntity setAdress(String adress) {
        if (!TextUtils.isEmpty(adress)) {
            this.adress = adress;
        }
        return this;
    }

    private RouteEntity setUri(Uri uri) {
        if (uri != null) {
            this.uri = uri;
        }
        return this;
    }

    public RouteEntity setDestination(Class<?> destination) {
        this.destination = destination;
        return this;
    }

    public RouteEntity putExtras(Bundle bundle) {
        this.mExtras = bundle;
        return this;
    }

    public RouteEntity setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public RouteEntity addFlags(int flags) {
        this.flags |= flags;
        return this;
    }

    public RouteEntity setOptions(Bundle options) {
        this.options = options;
        return this;
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     * @return current
     */
    public RouteEntity putString(@Nullable String key, @Nullable String value) {
        mExtras.putString(key, value);
        return this;
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean
     * @return current
     */
    public RouteEntity putBoolean(@Nullable String key, boolean value) {
        mExtras.putBoolean(key, value);
        return this;
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a short
     * @return current
     */
    public RouteEntity putShort(@Nullable String key, short value) {
        mExtras.putShort(key, value);
        return this;
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value an int
     * @return current
     */
    public RouteEntity putInt(@Nullable String key, int value) {
        mExtras.putInt(key, value);
        return this;
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a long
     * @return current
     */
    public RouteEntity putLong(@Nullable String key, long value) {
        mExtras.putLong(key, value);
        return this;
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a double
     * @return current
     */
    public RouteEntity putDouble(@Nullable String key, double value) {
        mExtras.putDouble(key, value);
        return this;
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a byte
     * @return current
     */
    public RouteEntity putByte(@Nullable String key, byte value) {
        mExtras.putByte(key, value);
        return this;
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a char
     * @return current
     */
    public RouteEntity putChar(@Nullable String key, char value) {
        mExtras.putChar(key, value);
        return this;
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a float
     * @return current
     */
    public RouteEntity putFloat(@Nullable String key, float value) {
        mExtras.putFloat(key, value);
        return this;
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence, or null
     * @return current
     */
    public RouteEntity putCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mExtras.putCharSequence(key, value);
        return this;
    }

    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Parcelable object, or null
     * @return current
     */
    public RouteEntity putParcelable(@Nullable String key, @Nullable Parcelable value) {
        mExtras.putParcelable(key, value);
        return this;
    }

    /**
     * Inserts an array of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an array of Parcelable objects, or null
     * @return current
     */
    public RouteEntity putParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mExtras.putParcelableArray(key, value);
        return this;
    }

    /**
     * Inserts a List of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList of Parcelable objects, or null
     * @return current
     */
    public RouteEntity putParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        mExtras.putParcelableArrayList(key, value);
        return this;
    }

    /**
     * Inserts a SparceArray of Parcelable values into the mapping of this
     * Bundle, replacing any existing value for the given key.  Either key
     * or value may be null.
     *
     * @param key   a String, or null
     * @param value a SparseArray of Parcelable objects, or null
     * @return current
     */
    public RouteEntity putSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        mExtras.putSparseParcelableArray(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public RouteEntity putIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mExtras.putIntegerArrayList(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public RouteEntity putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mExtras.putStringArrayList(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public RouteEntity putCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        mExtras.putCharSequenceArrayList(key, value);
        return this;
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Serializable object, or null
     * @return current
     */
    public RouteEntity putSerializable(@Nullable String key, @Nullable Serializable value) {
        mExtras.putSerializable(key, value);
        return this;
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a byte array object, or null
     * @return current
     */
    public RouteEntity putByteArray(@Nullable String key, @Nullable byte[] value) {
        mExtras.putByteArray(key, value);
        return this;
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a short array object, or null
     * @return current
     */
    public RouteEntity putShortArray(@Nullable String key, @Nullable short[] value) {
        mExtras.putShortArray(key, value);
        return this;
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a char array object, or null
     * @return current
     */
    public RouteEntity putCharArray(@Nullable String key, @Nullable char[] value) {
        mExtras.putCharArray(key, value);
        return this;
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a float array object, or null
     * @return current
     */
    public RouteEntity putFloatArray(@Nullable String key, @Nullable float[] value) {
        mExtras.putFloatArray(key, value);
        return this;
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence array object, or null
     * @return current
     */
    public RouteEntity putCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        mExtras.putCharSequenceArray(key, value);
        return this;
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Bundle object, or null
     * @return current
     */
    public RouteEntity putBundle(@Nullable String key, @Nullable Bundle value) {
        mExtras.putBundle(key, value);
        return this;
    }

    /**
     * Set normal transition anim
     *
     * @param enterAnim enter
     * @param exitAnim  exit
     * @return current
     */
    public RouteEntity addTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }


    public String getAdress() {
        return adress;
    }

    public Uri getUri() {
        return uri;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public Bundle getExtras() {
        return mExtras;
    }

    public int getFlags() {
        return flags;
    }

    public Bundle getOptions() {
        return options;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
    }


    public Object navigation() {
        return navigation(null, -1);
    }

    public Object navigation(Context currentContext) {
        return navigation(currentContext, -1);
    }

    public Object navigation(Context currentContext, int requestCode) {
        return Router.getInstance().navigation(this, currentContext, requestCode);
    }

}
