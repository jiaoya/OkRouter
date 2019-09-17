package com.albert.okrouter.provide;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.albert.okrouter.core.OkRouter;
import com.albert.okrouter.utils.ProcessUtil;
import com.albert.okrouter.utils.Rlog;

import java.util.HashMap;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-09-04.
 *      Desc         :
 * </pre>
 */
public class ProviderService extends Service {

    /**
     * 进程里的实例ation，Dispatcher里初始化
     */
    public static HashMap<String, IBaseAction> mActionInstances = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    IProviderAidlInterface.Stub iBinder = new IProviderAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public ActionResult getAction(String actionName, Bundle data) throws RemoteException {
            if (mActionInstances.size() == 0 || !mActionInstances.containsKey(actionName)) {
                return null;
            }
            Rlog.e("RemoteService", "getAction---" + actionName);
            ActionResult result = mActionInstances.get(actionName).invoke(ProviderService.this, data);
            return result;
        }

        @Override
        public boolean close() throws RemoteException {
            return OkRouter.getInstance().close(ProcessUtil.getCurrentProcessName());
        }
    };


}
