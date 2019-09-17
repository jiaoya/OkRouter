package com.albert.okrouter.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-09-02.
 *      Desc         :
 * </pre>
 */
public class ProcessUtil {

    public static final String UNKNOWN_PROCESS_NAME = "unknown_process_name";
    private static Application mApplicationContext;

    public static void init(Application application) {
        mApplicationContext = application;
    }

    /**
     * 判断是否运行在主进程
     * Return whether app running in the main process.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMainProcess() {
        return mApplicationContext.getPackageName().equals(getCurrentProcessName());
    }

    public static String getMainProcessName() {
        return mApplicationContext.getPackageName();
    }

    /**
     * 获取当前进程名称
     * Return the name of current process.
     *
     * @return the name of current process
     */
    public static String getCurrentProcessName() {
        ActivityManager am = (ActivityManager) mApplicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return null;
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) return null;
        int pid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.pid == pid) {
                if (aInfo.processName != null) {
                    return aInfo.processName;
                }
            }
        }
        return UNKNOWN_PROCESS_NAME;
    }

    /**
     * 获取当前进程id
     *
     * @return
     */
    public static int getCurrentProcessId() {
        return Process.myPid();
    }

    /**
     * 获取进程名
     *
     * @param pid
     * @return
     */
    public static String getProcessName(int pid) {
        String processName = UNKNOWN_PROCESS_NAME;
        try {
            File file = new File("/proc/" + pid + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
        }
        return UNKNOWN_PROCESS_NAME;
    }

    /**
     * 获取进程名
     *
     * @param context
     * @param pid
     * @return
     */
    public static String getProcessName(Context context, int pid) {
        String processName = getProcessName(pid);
        if (UNKNOWN_PROCESS_NAME.equals(processName)) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) {
                return UNKNOWN_PROCESS_NAME;
            }
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        } else {
            return processName;
        }
        return UNKNOWN_PROCESS_NAME;
    }

}
