package com.kandi.systemui.service;

import android.os.Handler;
import android.os.RemoteException;
import android.text.format.DateFormat;
import com.kandi.systemui.util.TimeUtils;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TboxController {
    KandiSystemUiService mService;
    Timer mTime;
    TimerTask mTimerTask;
    public boolean timeFlag =true;
    String locale ="ch" ;
    public TboxController(KandiSystemUiService service) {
        mService = service;
        addTboxObserve();
    }
    public void addTboxObserve(){

        mTime = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                //wifiIntensity = getWifiIntensity();
                mHandler.sendEmptyMessage(0);
            }
        };
        mTime.schedule(mTimerTask, 5000, 1000);
    }
    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
//                refreshTime();

            }

        };
    };
}
