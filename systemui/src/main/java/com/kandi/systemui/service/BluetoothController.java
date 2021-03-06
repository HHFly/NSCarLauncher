package com.kandi.systemui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BluetoothController extends BroadcastReceiver {

    private static final String BluetoothService = "android.os.IKdBtService";
    public final static String ACTION_REQUEST_PINCODE = "com.kangdi.BroadCast.RequestPinCode";
    public final static String ACTION_RINGCALL = "com.kangdi.BroadCast.RingCall";
    public final static String ACTION_CALLSTART = "com.kangdi.BroadCast.CallStart";
    public final static String ACTION_CALLEND = "com.kangdi.BroadCast.CallEnd";
    public final static String ACTION_HC = "com.kangdi.BroadCast.HandsFreeConnect";
    public final static String ACTION_HD = "com.kangdi.BroadCast.HandsFreeDisconnect";
    public final static String ACTION_AC = "com.kangdi.BroadCast.AudioConnect";
    public final static String ACTION_AD = "com.kangdi.BroadCast.AudioDisconnect";

    KandiSystemUiService mService;

    public BluetoothController(KandiSystemUiService service) {
        mService = service;
        addBluetoothReceiver();
        //startService(mService);
    }

    boolean isBluetoothHandsFreeEnable = false;
    boolean isBluetoothAudioEnable = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        /*
         * if (intent.getAction().equals(ACTION_HC)||intent.getAction().equals(
         * ACTION_AC)) { mService.setBluetoothState(true); }else if
         * (intent.getAction
         * ().equals(ACTION_HD)||intent.getAction().equals(ACTION_AD)){
         * mService.setBluetoothState(false); }
         */
        Log.d("huachao","BluetoothController:"+intent.getAction());
        if (intent.getAction().equals(ACTION_REQUEST_PINCODE)) {
            return ;
        }

        if (intent.getAction().equals(ACTION_HC)) {
            isBluetoothHandsFreeEnable = true;
        } else if (intent.getAction().equals(ACTION_AC)) {
            isBluetoothAudioEnable = true;
        } else if (intent.getAction().equals(ACTION_HD)) {
            isBluetoothHandsFreeEnable = false;
        } else if (intent.getAction().equals(ACTION_AD)) {
            isBluetoothAudioEnable = false;
        }
        mService.setBluetoothState(isBluetoothAudioEnable
                || isBluetoothHandsFreeEnable);
    }

    public void addBluetoothReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(ACTION_HC);
        iFilter.addAction(ACTION_HD);
        iFilter.addAction(ACTION_AC);
        iFilter.addAction(ACTION_AD);
        iFilter.addAction(ACTION_REQUEST_PINCODE);
        iFilter.addAction(ACTION_RINGCALL);
        iFilter.addAction(ACTION_CALLSTART);
        iFilter.addAction(ACTION_CALLEND);
        mService.registerReceiver(this, iFilter);
    }

    public void removeBluetoothReceiver() {
        mService.unregisterReceiver(this);
    }
/*
    private IKdBtService R_service = null;
    private Context context;

    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            R_service = IKdBtService.Stub.asInterface(arg1);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            R_service = null;
        }

    };



    public void startService(Context context) {
        this.context = context;
        Intent it = new Intent(BluetoothService);
        this.context.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        if (R_service != null) {
            this.context.unbindService(this.serviceConnection);
        } else {
        }
    }
*/
}
