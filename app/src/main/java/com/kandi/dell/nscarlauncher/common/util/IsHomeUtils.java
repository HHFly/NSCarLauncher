package com.kandi.dell.nscarlauncher.common.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class IsHomeUtils {
    public static boolean isHome(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        Log.d("SystemUI",rti.get(0).topActivity.getPackageName());
        return "com.kandi.nscarlauncher".equals(rti.get(0).topActivity.getPackageName());
    }
}
