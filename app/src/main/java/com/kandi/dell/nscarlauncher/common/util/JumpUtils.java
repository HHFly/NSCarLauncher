package com.kandi.dell.nscarlauncher.common.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;

public class JumpUtils {

    public  static void actActivity(Activity act, String ca){
        Intent intent =new Intent(ca);
        act.startActivity(intent);
        act.overridePendingTransition(0,0);
    }
    public  static void actActivity(Activity act, Class ca){
        Intent intent =new Intent(act,ca);
        act.startActivity(intent);
        act.overridePendingTransition(0,0);
    }
    public static void  actAPK(Activity act, @FragmentType int type){
        switch (type){
            case FragmentType.AIRCONTROLL:
                actActivity(act,"com.kandi.aircontrol","com.kandi.aircontrol.view.AirCtrlActivity");
                break;
            case FragmentType.CARCONTROLL:
                actActivity(act,"com.kandi.carcontrol","com.kandi.carcontrol.view.CarCtrlActivity");
                break;
            case FragmentType.CARSET:
                actActivity(act,"com.kandi.acarset","com.kandi.acarset.CarSetDriveSetActivity");
                break;
            case FragmentType.POWERRECOVER:
                actActivity(act,"com.kandi.acarpower","com.kandi.acarpower.activity.PowerRecoveryActivity");
                break;
            case FragmentType.CARPOWER:
                actActivity(act,"com.kandi.powermanager","com.kandi.powermanager.view.PowerManagerActivity");
                break;
        }
    }
    public  static void actActivity(Activity act, String classname,String main){
        Intent intent =new Intent();
        intent.setClassName(classname,main);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        try {
            act.startActivity(intent);
            act.overridePendingTransition(0,0);
        }catch (Exception e){
            Toast.makeText(act, "未安装该应用", Toast.LENGTH_SHORT).show();
        }

    }
}
