package com.kandi.dell.nscarlauncher.ui_portrait.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;



public class USBReceover extends BroadcastReceiver {
    public static final String PATH_USB = "/mnt/media_rw/udisk/";
    boolean isSearch = false;
    public  Boolean  isUsbOuting =false;//设备是否移除
    HomePagerActivity homePagerActivity;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            Toast.makeText(context, R.string.USB接入, Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                int loopcount =0;
                @Override
                public void run() {

                    if (!isSearch) {
                        while (true) {

                            try {
                                Thread.sleep(1000);
                                isSearch =true;
                                if(isUsbOuting){
                                    Log.d("USBReceover", "onReceive: " +isUsbOuting);
                                    continue;
                                }
                                boolean exist = isUdiskExist();


                                Log.d("USBReceover", "exist:" + exist);
                                if (exist) {
                                    File file = new File(PATH_USB);
                                    if(!file.exists()){
                                        continue;
                                    }
                                    if(isUdiskExist()){
                                        Thread.sleep(3000);
                                    }else {
                                        continue;
                                    }

//                                    homePagerActivity.getScanService().usbStatus = true;
                                    if (!intent.getExtras().getBoolean("userStatus", false)) {
                                        Log.d("USBReceover", "run: 1");
                                        homePagerActivity.getScanService().ScanVideoMusic(context, 0);
                                    }
                                    isSearch =false;
                                    break;
                                }else {
                                    loopcount++;
                                    if (loopcount>50){
                                        break;
                                    }
                                }


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }).start();
        }
        else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            Toast.makeText(context, R.string.USB拔出, Toast.LENGTH_SHORT).show();
            homePagerActivity.getScanService().ScanVideoMusic(context,0);
            isUsbOuting =true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        isUsbOuting=false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }



    public  boolean isUdiskExist() {
             Log.d("StorageDeviceManager", "isUdiskExist");

                String path = "/proc/mounts";

               boolean ret = false;
           try {
                String encoding = "GBK";
                 File file = new File(path);
              if ((file.isFile()) && (file.exists()))
             {
                 InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
              BufferedReader bufferedReader = new BufferedReader(read);
             String lineTxt = null;
             while (((lineTxt = bufferedReader.readLine()) != null) && (!ret)) {
                String[] a = lineTxt.split(" ");//将读出来的一行字符串用 空格 来分割成字符串数组并存储进数组a里面
             String str = a[0];//取出位置0处的字符串

              if ((str.contains("/dev/block/vold")) &&
              (a[1].contains("udisk"))) {
              ret = true;
             }
           }

             read.close();
    } else {
            Log.d("StorageDeviceManager", "can't find file: " + path);
            }
        } catch (Exception e) {
e.printStackTrace();
}

return ret;
 }

    public USBReceover(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
        addReceiver();
    }
    public void addReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        iFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");

        homePagerActivity.registerReceiver(this, iFilter);
    }
}
