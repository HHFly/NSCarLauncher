package com.example.dell.nscarlauncher.ui.home.androideunm;

import android.support.annotation.IntDef;

@IntDef({
        HandleKey.TIME,
        HandleKey.WEATHAER,
        HandleKey.POWER,
        HandleKey.SPEED
})
public @interface HandleKey  {
//    时间
    int TIME    =1;

    /*天气*/
    int WEATHAER=2;
    /*电量*/
    int POWER =3;
//    速度
    int SPEED =4;
}
