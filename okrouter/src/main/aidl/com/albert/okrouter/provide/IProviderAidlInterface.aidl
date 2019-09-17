// IProviderAidlInterface.aidl
package com.albert.okrouter.provide;

// Declare any non-default types here with import statements
import com.albert.okrouter.provide.ActionResult;
import android.os.Bundle;

interface IProviderAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

     ActionResult getAction(String actionName,inout Bundle data);

     boolean close();
}
