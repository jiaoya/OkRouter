package com.albert.okrouter.module1;

import android.content.Context;
import android.os.Bundle;

import com.albert.okrouter.annotation.Action;
import com.albert.okrouter.provide.ActionResult;
import com.albert.okrouter.provide.IBaseAction;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-09-08.
 *      Desc         :
 * </pre>
 */
@Action(processName = "com.albert.okrouter.module1", address = "Model1TestAction")
public class Model1TestAction implements IBaseAction {

    @Override
    public ActionResult invoke(Context context, Bundle bundle) {
        String a = bundle.getString("test", "==");
        ActionResult result = new ActionResult();
        result.setStringData("Model1TestAction:" + a);
        return result;
    }

}
