package com.kandi.dell.nscarlauncher.ui.application;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.kandi.dell.nscarlauncher.base.model.BaseModel;

public class AppInfo extends BaseModel {
    private String appLabel;    //应用程序标签
    private Drawable appIcon ;  //应用程序图像

    private String pkgName ;    //应用程序所对应的包名
    private String name ;//应用名
    public AppInfo(){}

    public String getAppLabel() {
        return appLabel;
    }
    public void setAppLabel(String appName) {
        this.appLabel = appName;
    }
    public Drawable getAppIcon() {
        return appIcon;
    }
    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPkgName(){
        return pkgName ;
    }
    public void setPkgName(String pkgName){
        this.pkgName=pkgName ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
