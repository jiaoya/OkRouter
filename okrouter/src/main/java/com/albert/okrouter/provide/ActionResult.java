package com.albert.okrouter.provide;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-21.
 *      Desc         :
 * </pre>
 */
public class ActionResult<T> implements Parcelable {

    public String processName;
    public String actionName;
    private String stringData;
    private Integer intData;
    private Float floatData;
    private Long longData;
    private Boolean bleData;
    private T data;

    public ActionResult() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.processName);
        dest.writeString(this.actionName);
        dest.writeString(this.stringData);
        dest.writeValue(this.intData);
        dest.writeValue(this.floatData);
        dest.writeValue(this.longData);
        dest.writeValue(this.bleData);
        dest.writeParcelable((Parcelable) this.data, flags);
    }

    protected ActionResult(Parcel in) {
        this.processName = in.readString();
        this.actionName = in.readString();
        this.stringData = in.readString();
        this.intData = (Integer) in.readValue(Integer.class.getClassLoader());
        this.floatData = (Float) in.readValue(Float.class.getClassLoader());
        this.longData = (Long) in.readValue(Long.class.getClassLoader());
        this.bleData = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.data = (T) in.readParcelable(this.getClass().getClassLoader());
    }

    public static final Creator<ActionResult> CREATOR = new Creator<ActionResult>() {
        @Override
        public ActionResult createFromParcel(Parcel source) {
            return new ActionResult(source);
        }

        @Override
        public ActionResult[] newArray(int size) {
            return new ActionResult[size];
        }
    };

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getStringData() {
        return stringData;
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    public Integer getIntData() {
        return intData;
    }

    public void setIntData(Integer intData) {
        this.intData = intData;
    }

    public Float getFloatData() {
        return floatData;
    }

    public void setFloatData(Float floatData) {
        this.floatData = floatData;
    }

    public Long getLongData() {
        return longData;
    }

    public void setLongData(Long longData) {
        this.longData = longData;
    }

    public Boolean getBleData() {
        return bleData;
    }

    public void setBleData(Boolean bleData) {
        this.bleData = bleData;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        if (data.getClass() == String.class ||
                data.getClass() == Integer.class ||
                data.getClass() == Long.class ||
                data.getClass() == Float.class ||
                data.getClass() == Boolean.class) {
            throw new RuntimeException("模板，不允许为基本类型");
        }
        this.data = data;
    }
}
