package com.kandi.systemui.service;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.Log;

import com.kandi.systemui.base.App;
import com.kandi.systemui.driver.DriverServiceManger;
import com.kandi.systemui.driver.EcocEnergyInfoDriver;
import com.kandi.systemui.util.TimeUtils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatteryAndTimeController {
    KandiSystemUiService mService;
    Timer mTime;
    TimerTask mTimerTask;
    public boolean timeFlag =true;
    String locale ="ch" ;
    public BatteryAndTimeController(KandiSystemUiService service) {
        mService = service;
        addBatteryAndTimeObserve();
    }

    private void refreshPannel() {
        EcocEnergyInfoDriver model = DriverServiceManger.getInstance().getEcocEnergyInfoDriver();
        //EnergyInfoDriver model = DriverServiceManger.getInstance().getEnergyInfoDriver();

        if(model != null) {
            try {
                model.retreveGeneralInfo();
            }catch (RemoteException e) {
                e.printStackTrace();
                return;
            }
            if (model.getCargingState() == 1) {
                mService.setBetteryLevel(-1);
            }else {
                mService.setBetteryLevel((int)model.getSOC());
            }
            mService.setRemainMileage((int) model.getSOC());
            Log.d("Power", "CargingState:" + model.getCargingState() + ":Bettery:" + model.getSOC() + ":RemainMileage:" + model.getRemainMileage());
        }
        else {
            mService.restartKdService();
        }
    }

    private void refreshTime() {
//        Date date = new Date();
//
//        SimpleDateFormat sdf =DateFormat.is24HourFormat(mService.getApplicationContext())? new SimpleDateFormat("HH:mm"):new SimpleDateFormat("HH:mm");
//
//        String dateStr = sdf.format(date);
        mService.setCurrentTime(DateFormat.is24HourFormat( mService.getApplicationContext())?TimeUtils.getHour(): TimeUtils.getHour_Min12());
    }

    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                refreshPannel();
//                refreshTime();
                WifiController.update(mService);
            }
            if(msg.what==1){
                mService.setCurrentTime(DateFormat.is24HourFormat( mService.getApplicationContext())?TimeUtils.getHour(): TimeUtils.getHour_Min12());
                String language = Locale.getDefault().getLanguage();
                if(!locale.equals(language)){
                    locale= language;
                    mService.setLocal(locale);
                }
            }
        };
    };
    
    public void addBatteryAndTimeObserve(){
        init_time();
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
    public void removeBatteryAndTimeObserve(){
        mTime.cancel();
    }

    //日期 时间模块初始化
    private void init_time() {
        ExecutorService timePool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        timePool.execute(new Runnable() {
            @Override
            public void run() {

                while (timeFlag) {
                    try {
                        //延时一秒作用
                        mHandler.sendEmptyMessage(1);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
