package com.kandi.systemui.base;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

public class App  extends MultiDexApplication {
    private static App s_app;

    public static App get() {
        return s_app;
    }

    @Override
    public void onCreate() {
        s_app = this;
        super.onCreate();

    }
}
