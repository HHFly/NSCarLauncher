package com.kandi.dell.nscarlauncher.common.util;

import android.app.ActivityManager;
import android.content.Context;

public class MemoryUtil {
    // 用于获取手机可用内存
    private static ActivityManager mActivityManager;

    //获取当前可用内存，返回数据以字节为单位。
    //param context 可传入应用程序上下文。
    //@return 当前可用内存。
    public static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }

        // 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
        // @param context 可传入应用程序上下文。
        // @return ActivityManager的实例，用于获取手机可用内存。
        private static ActivityManager getActivityManager(Context context) {
            if (mActivityManager == null) {
                mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            }
            return mActivityManager;
        }
}
