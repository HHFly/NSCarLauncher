package com.example.dell.nscarlauncher.common.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuwh on 2017/10/7.
 */

public class PDLifecycleHandle implements Application.ActivityLifecycleCallbacks {

    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    private static Activity s_activity;

    private static List<Activity> mActivitys = new ArrayList<Activity>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivitys.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        s_activity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        s_activity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivitys.remove(activity);
    }

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        // 当所有 Activity 的状态中处于 resumed 的大于 paused 状态的，即可认为有Activity处于前台状态中
        return resumed > paused;
    }

    public static Activity currentActivity(){
        return s_activity;
    }

    public static void exit(){
        for (Activity activity:mActivitys){
            if (activity!=null){
                activity.finish();
            }
        }
        System.exit(0);
    }

}
