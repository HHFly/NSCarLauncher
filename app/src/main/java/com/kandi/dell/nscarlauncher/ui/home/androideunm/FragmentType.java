package com.kandi.dell.nscarlauncher.ui.home.androideunm;

import android.support.annotation.IntDef;

@IntDef({
        FragmentType.FM,
        FragmentType.BTMUSIC,
        FragmentType.MUSIC,
        FragmentType.PHONE,
        FragmentType.SET,
        FragmentType.APPLICATION,
        FragmentType.AIRCONTROLL,
        FragmentType.CARCONTROLL,
        FragmentType.CARSET,
        FragmentType.CARPOWER,
        FragmentType.POWERRECOVER,
        FragmentType.VIDEO,
        FragmentType.BTSET
})
public @interface FragmentType {
    /*电台*/
    int FM =11;
/*蓝牙音乐*/
    int BTMUSIC =1;
    /*本地音乐*/
    int MUSIC =2;
    /*电话*/
    int PHONE=3;
/*设置*/
    int SET =4;
    /*应用*/
    int APPLICATION =5;
    /*空调*/
    int AIRCONTROLL =6;
    /*车辆控制*/
    int CARCONTROLL =7;
    /*车辆设置*/
    int CARSET =8;
    /*车辆电池管理*/
    int CARPOWER= 9;
    /*车辆能量回收*/
    int POWERRECOVER=10;
    /*视频*/
    int VIDEO =12;
    /*蓝牙设置*/
    int BTSET =13;
}
