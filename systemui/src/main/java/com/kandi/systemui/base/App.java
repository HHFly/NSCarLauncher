package com.kandi.systemui.base;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.view.WindowManager;

public class App  extends MultiDexApplication {
    private static App s_app;
    private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();
    public static App get() {
        return s_app;
    }
    public WindowManager.LayoutParams getMywmParams(){
        return wmParams;
    }
    @Override
    public void onCreate() {
        s_app = this;
        super.onCreate();

    }
}
