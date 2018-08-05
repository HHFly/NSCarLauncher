package com.example.dell.nscarlauncher.app;


import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.dell.nscarlauncher.BuildConfig;
import com.example.dell.nscarlauncher.common.FrescoUtils;
import com.example.dell.nscarlauncher.common.PDLifecycleHandle;
import com.example.dell.nscarlauncher.common.ToastUtils;
import com.white.lib.utils.UtilsConfig;


/**
 * Created by zhuwh on 2018/4/10.
 */

public class
App extends Application {

    private static App s_app;

    public static App get() {
        return s_app;
    }


    private Activity mCurActivity;



    @Override
    public void onCreate() {
        super.onCreate();

        s_app = this;
        //初始化服务

        ToastUtils.init(this);
        registerActivityLifecycleCallbacks(new PDLifecycleHandle());
        //初始化Fresco
        FrescoUtils.initialize(this);
        //初始化white框架
        UtilsConfig
                .getInstance(this)
                .setLogOpen(BuildConfig.IS_OPEN_LOG);

    }



    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                mCurActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 获取当前Activity
     * @param <T>
     * @return
     */
    public <T extends Activity> T getCurrentActivity() {
        return (T) mCurActivity;
    }

    /**
     * 获取当前Activity的fragmentManager
     * @return
     */
    public FragmentManager getFragmentManager() {
        if (mCurActivity == null) {
            throw new NullPointerException("mCurActivity is null");
        }
        return mCurActivity.getFragmentManager();
    }




    /**
     * 获取当前Activity
     *
     * @return
     */
    public Activity getCurActivity() {
        return PDLifecycleHandle.currentActivity();
    }
}
