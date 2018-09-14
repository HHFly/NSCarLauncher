package com.kandi.systemui.service;

import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.kandi.systemui.driver.DriverServiceManger;
import com.kandi.systemui.driver.EcocEnergyInfoDriver;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BatteryAndTimeController {
    KandiSystemUiService mService;
    Timer mTime;
    TimerTask mTimerTask;
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
                mService.setBetteryLevel((int)model.getSOC() * 10);
            }
            mService.setRemainMileage((int) model.getSOC());
            Log.d("huachao", "CargingState:" + model.getCargingState() + ":Bettery:" + model.getSOC() + ":RemainMileage:" + model.getRemainMileage());
        }
        else {
            mService.restartKdService();
        }
    }

    private void refreshTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String dateStr = sdf.format(date);
        mService.setCurrentTime(dateStr);
    }

    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                refreshPannel();
                refreshTime();
                WifiController.update(mService);
            }
        };
    };
    
    public void addBatteryAndTimeObserve(){
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
}
