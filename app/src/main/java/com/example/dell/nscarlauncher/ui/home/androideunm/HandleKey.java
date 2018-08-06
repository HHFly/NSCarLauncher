package com.example.dell.nscarlauncher.ui.home.androideunm;

import android.support.annotation.IntDef;

@IntDef({
        HandleKey.TIME,
        HandleKey.WEATHAER
})
public @interface HandleKey  {
//    时间
    int TIME    =1;

    /*天气*/
    int WEATHAER=2;
}
