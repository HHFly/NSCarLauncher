package com.example.dell.nscarlauncher.ui.home;

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
