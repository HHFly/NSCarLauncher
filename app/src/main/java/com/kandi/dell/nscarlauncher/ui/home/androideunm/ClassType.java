package com.kandi.dell.nscarlauncher.ui.home.androideunm;

import android.support.annotation.IntDef;

@IntDef({
        ClassType.FM,
      ClassType.WEATHAER
})
public @interface ClassType {
    /*电台*/
    int FM =0;

    /*天气*/
    int WEATHAER=1;
}
