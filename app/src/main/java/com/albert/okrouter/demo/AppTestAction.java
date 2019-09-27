package com.albert.okrouter.demo;

import android.content.Context;
import android.os.Bundle;

import com.albert.okrouter.annotation.Action;
import com.albert.okrouter.provide.ActionResult;
import com.albert.okrouter.provide.IBaseAction;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-09-05.
 *      Desc         :
 * </pre>
 */
@Action(processName = "com.albert.okrouter.demo", adress = "AppTestAction")
public class AppTestAction implements IBaseAction {

    @Override
    public ActionResult invoke(Context context, Bundle bundle) {
        ActionResult result = new ActionResult();
        result.setStringData("jiaoya+AppTestAction");
        return result;
    }

}
