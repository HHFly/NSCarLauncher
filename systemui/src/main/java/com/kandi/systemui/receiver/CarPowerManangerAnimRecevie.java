package com.kandi.systemui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.kandi.systemui.driver.EcocEnergyInfoDriver;
import com.kandi.systemui.service.KandiSystemUiService;

import java.util.Set;

public class CarPowerManangerAnimRecevie extends BroadcastReceiver {
    KandiSystemUiService mService;
    public CarPowerManangerAnimRecevie(KandiSystemUiService service) {
        mService = service;
        addReceiver();
        //startService(mService);
    }
    public void addReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("com.driverlayer.kdos_driverserver");
        mService.registerReceiver(this, iFilter);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.driverlayer.kdos_driverserver")){
            final  String m_key= "KD_CAST_EVENT";
            final String skeyChargeGun ="KD_CAST_EVENT1";
            final String skeyChargeState ="KD_CAST_EVENT2";
            Bundle bundle =intent.getExtras();
            Set<String> keySet = bundle.keySet();
            for(String key :keySet){
                if(!key.startsWith(m_key)){
                    continue;
                }
                int eventId;
                try {
                    eventId= Integer.parseInt(key.substring(m_key.length()));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    continue;
                }
                if(1==eventId){
                    //充电枪插拔事件，
                    boolean isGunIn =bundle.getBoolean(skeyChargeGun);//Value值true是充电枪插入，false是充电枪拔出。
                    if(isGunIn){
                        mService.showGunInDialog();
                    }
                }

                if(2==eventId){
                    //充电启停事件
                    int state =bundle.getInt(skeyChargeState);//Value值0是停止充电，1是开始充电。2 故障
                    if(0==state){
                        EcocEnergyInfoDriver.debugChargingState=0;
                        mService.dissGunInDialog();
                        mService.dissPowerInDialog();
                    }
                    if(1==state){
                        EcocEnergyInfoDriver.debugChargingState=1;
                        mService.dissGunInDialog();
                        mService.showPowerInDialog();
                        if(mService.getDialogPowerIn().isShow){
                            mService.getDialogPowerIn().setPowerError(false);
                        }

                    }
                    if(2==state){
                        EcocEnergyInfoDriver.debugChargingState=0;
                        mService.dissGunInDialog();
                        mService.showPowerInDialog();
                       if(mService.getDialogPowerIn().isShow){
                           mService.getDialogPowerIn().setPowerError(true);
                       }
                    }
                }
            }

        }
    }
}
