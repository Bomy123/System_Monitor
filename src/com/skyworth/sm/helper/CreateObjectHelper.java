package com.skyworth.sm.helper;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by zhangmingbao on 17-2-7.
 */

public class CreateObjectHelper {
   private static PackageManager pm = null;
    public static PackageManager getPmInstance(Context context)
    {
        if(pm == null)
        {

            pm = context.getPackageManager();
        }
        return pm;
    }
}
