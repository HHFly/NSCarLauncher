package com.kandi.dell.nscarlauncher.ui.home.androideunm;

import android.support.annotation.IntDef;

@IntDef({
        HandleKey.TIME,
        HandleKey.WEATHAER,
        HandleKey.POWER,
        HandleKey.SPEED,
        HandleKey.FRAME,
        HandleKey.FM,
        HandleKey.BTMUSICCOLSE,
        HandleKey.BTMUSICOPEN,
        HandleKey.AIROPEN,
        HandleKey.AIRCLOSE,
        HandleKey.FMNEXT,
        HandleKey.FMPREV,
        HandleKey.SHOW
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
    /*显示主界面*/
    int FRAME =6;
    /*fm*/
    int  FM =7;
    /*btmusic*/
    int BTMUSICCOLSE=8;

    int BTMUSICOPEN=9;
    /*空调开*/
    int AIROPEN=10;
    /*空调关*/
    int AIRCLOSE=11;

    /*fm关*/
    int  OPEMFM =12;
    int FMNEXT = 13;
    int FMPREV = 14;
    int SHOW =15;
    int MUSICOPEN =16;
    int MUSICCOLSE =17;
}
