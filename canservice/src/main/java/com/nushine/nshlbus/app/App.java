package com.nushine.nshlbus.app;

import android.support.multidex.MultiDexApplication;
import android.util.SparseArray;

import com.nushine.nshlbus.com.driverlayer.os_driverInfo.DriverInfomation;

public class App extends MultiDexApplication {
    private static App s_app;
    public static App get() {
        return s_app;
    }
    private SparseArray btyesData ;
    public DriverInfomation m_myinfo;
    public DriverInfomation getM_myinfo() {
        return m_myinfo;
    }
    private boolean isCharging=false;
    public boolean canStatus = true;//true能正常通讯 false通讯异常
    public boolean isDestroy = false;

    public void setM_myinfo(DriverInfomation m_myinfo) {
        this.m_myinfo = m_myinfo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        s_app = this;
    }

    public SparseArray getBtyesData() {
        if(btyesData==null){
            btyesData=new SparseArray();
        }
        return btyesData;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    public boolean isCanStatus() {
        return canStatus;
    }

    public void setCanStatus(boolean canStatus) {
        this.canStatus = canStatus;
    }

    public void setDestroy(boolean destroy) {
        isDestroy = destroy;
    }

    public boolean isDestroy() {
        return isDestroy;
    }
}
