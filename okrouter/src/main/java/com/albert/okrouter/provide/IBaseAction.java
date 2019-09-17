package com.albert.okrouter.provide;

import android.content.Context;
import android.os.Bundle;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-08-21.
 *      Desc         :
 * </pre>
 */
public interface IBaseAction {

    ActionResult invoke(Context context, Bundle bundle);

}
