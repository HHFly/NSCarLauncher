package com.example.dell.nscarlauncher.common.util;

import android.app.Activity;
import android.content.Intent;

public class JumpUtils {

    public  static void actActivity(Activity act, String ca){
        Intent intent =new Intent(ca);
        act.startActivity(intent);
        act.overridePendingTransition(0,0);
    }
}
