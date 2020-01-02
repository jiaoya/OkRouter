package com.albert.okrouter.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import com.albert.okrouter.BuildConfig;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-14.
 *      Desc         :
 * </pre>
 */
public class Rlog {

    private static boolean showLog = BuildConfig.DEBUG;

    public static void setShowLog(boolean showLog) {
        Rlog.showLog = showLog;
    }

    public static void d(String tag, @Nullable String o) {
        if (showLog) {
            Log.d(tag, o);
        }
    }

    public static void e(String tag, @Nullable String o) {
        if (showLog) {
            Log.e(tag, o);
        }
    }
}
