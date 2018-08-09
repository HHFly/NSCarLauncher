package com.example.dell.nscarlauncher.ui.home.androideunm;

import android.support.annotation.IntDef;

@IntDef({
        FragmentType.FM,
        FragmentType.BTMUSIC
})
public @interface FragmentType {
    /*电台*/
    int FM =0;
/*蓝牙音乐*/
    int BTMUSIC =1;
}
