package com.example.dell.nscarlauncher.ui.setting.eumn;

import android.support.annotation.IntDef;

@IntDef({
        SetType.ABOUT,
        SetType.DATE,
        SetType.DISPLAY,
        SetType.UPDATE,
        SetType.BT,
        SetType.WIFI,
        SetType.LANGUAGE,
        SetType.EQULIZER,
        SetType.CARSET,
        SetType.RECOVERY
})
public @interface SetType {
    /*显示*/
    int DISPLAY =1;
    /*蓝牙*/
    int BT =2;
    /*WIFI*/
    int WIFI =3;
    /*语音设置*/
    int LANGUAGE=4;
    /*均衡*/
    int  EQULIZER=5;
    /*日期时间*/
    int DATE =6;
    /*车辆设置*/
    int CARSET=7;
    /*能量回收*/
    int  RECOVERY =8;
    /*升级*/
    int UPDATE =9;
    /*关于*/
    int ABOUT =10;
    /*帮助*/
    int HELP =11;
}
