package com.albert.okrouter.core;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import com.albert.okrouter.RouterConstant;
import com.albert.okrouter.provide.ActionCallback;
import com.albert.okrouter.provide.IBaseAction;
import com.albert.okrouter.thread.RouterScheduler;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-09-02.
 *      Desc         :
 * </pre>
 */
public class ActionEntity {

    private Context mContext;
    private String processName;
    private Uri actionUri;
    private String actionAdress;
    private boolean isMainThread;        // 发起请求的线程是否是主线程
    private RouterScheduler scheduler = RouterScheduler.NORMAL;
    private Bundle mData;


    public ActionEntity() {
        this(null, null);
    }

    public ActionEntity(String processName, @Nullable String adress) {
        this.processName = processName;
        this.actionAdress = adress;
        // 设置成uri的形式
        if (TextUtils.isEmpty(processName)) {
            this.actionUri = Uri.parse(RouterConstant.ACTION_KEY + "&adress=" + adress);
        } else {
            this.actionUri = Uri.parse(RouterConstant.ACTION_KEY + processName + "&adress=" + adress);
        }

        this.mData = new Bundle();
    }

    public ActionEntity setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public void setMainThread(boolean mainThread) {
        isMainThread = mainThread;
    }

    public ActionEntity callbackOn(RouterScheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public ActionEntity putData(Bundle data) {
        this.mData = data;
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
    public ActionEntity putString(@Nullable String key, @Nullable String value) {
        mData.putString(key, value);
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
    public ActionEntity putBoolean(@Nullable String key, boolean value) {
        mData.putBoolean(key, value);
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
    public ActionEntity putShort(@Nullable String key, short value) {
        mData.putShort(key, value);
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
    public ActionEntity putInt(@Nullable String key, int value) {
        mData.putInt(key, value);
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
    public ActionEntity putLong(@Nullable String key, long value) {
        mData.putLong(key, value);
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
    public ActionEntity putDouble(@Nullable String key, double value) {
        mData.putDouble(key, value);
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
    public ActionEntity putByte(@Nullable String key, byte value) {
        mData.putByte(key, value);
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
    public ActionEntity putChar(@Nullable String key, char value) {
        mData.putChar(key, value);
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
    public ActionEntity putFloat(@Nullable String key, float value) {
        mData.putFloat(key, value);
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
    public ActionEntity putCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mData.putCharSequence(key, value);
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
    public ActionEntity putParcelable(@Nullable String key, @Nullable Parcelable value) {
        mData.putParcelable(key, value);
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
    public ActionEntity putParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mData.putParcelableArray(key, value);
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
    public ActionEntity putParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        mData.putParcelableArrayList(key, value);
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
    public ActionEntity putSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        mData.putSparseParcelableArray(key, value);
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
    public ActionEntity putIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mData.putIntegerArrayList(key, value);
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
    public ActionEntity putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mData.putStringArrayList(key, value);
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
    public ActionEntity putCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        mData.putCharSequenceArrayList(key, value);
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
    public ActionEntity putSerializable(@Nullable String key, @Nullable Serializable value) {
        mData.putSerializable(key, value);
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
    public ActionEntity putByteArray(@Nullable String key, @Nullable byte[] value) {
        mData.putByteArray(key, value);
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
    public ActionEntity putShortArray(@Nullable String key, @Nullable short[] value) {
        mData.putShortArray(key, value);
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
    public ActionEntity putCharArray(@Nullable String key, @Nullable char[] value) {
        mData.putCharArray(key, value);
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
    public ActionEntity putFloatArray(@Nullable String key, @Nullable float[] value) {
        mData.putFloatArray(key, value);
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
    public ActionEntity putCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        mData.putCharSequenceArray(key, value);
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
    public ActionEntity putBundle(@Nullable String key, @Nullable Bundle value) {
        mData.putBundle(key, value);
        return this;
    }


    public String getProcessName() {
        return processName;
    }

    public Uri getActionUri() {
        return actionUri;
    }


    public String getActionAdress() {
        return actionAdress;
    }


    public boolean isMainThread() {
        return isMainThread;
    }

    public Context getContext() {
        return mContext;
    }

    public Bundle getData() {
        return mData;
    }

    public RouterScheduler getScheduler() {
        return scheduler;
    }

    public IBaseAction getAction() {
        return Router.getInstance().connect(this);
    }

    public void getAction(ActionCallback callback) {
        Router.getInstance().connectThread(this, callback);
    }


}
