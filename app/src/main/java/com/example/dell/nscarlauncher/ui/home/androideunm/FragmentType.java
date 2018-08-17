package com.example.dell.nscarlauncher.ui.home.androideunm;

import android.support.annotation.IntDef;

@IntDef({
        FragmentType.FM,
        FragmentType.BTMUSIC,
        FragmentType.MUSIC,
        FragmentType.PHONE,
        FragmentType.SET,
        FragmentType.APPLICATION
})
public @interface FragmentType {
    /*电台*/
    int FM =0;
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
}
