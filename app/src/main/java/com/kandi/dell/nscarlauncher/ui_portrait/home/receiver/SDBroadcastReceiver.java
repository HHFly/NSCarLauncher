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




public class SDBroadcastReceiver extends BroadcastReceiver {
    public static final String PATH_SD = "/mnt/media_rw/extsd/";
    boolean isSearch = false;
    public  Boolean  isSDOuting =false;//设备是否移除
    public  Boolean isFirst =true;
    HomePagerActivity homePagerActivity;
    @Override
    public void onReceive(final Context context, final Intent intent) {

        String action = intent.getAction();
        Log.d("USB", "onReceive: " +action);
        switch (action){
            case Intent.ACTION_MEDIA_MOUNTED:
                if(!isFirst) {
                    Toast.makeText(context, R.string.设备接入, Toast.LENGTH_SHORT).show();
                }else {
                    isFirst=false;
                }
                new Thread(new Runnable() {
                    int loopcount =0;
                    @Override
                    public void run() {

                        if (!isSearch) {
                            while (true) {

                                try {
                                    Thread.sleep(1000);
                                    isSearch =true;
//                                File file = new File(PATH_SD);
                                    if(isSDOuting){
                                        Log.d("USBReceover", "onReceive: " +isSDOuting);
                                        continue;
                                    }
                                    boolean exist = isUdiskExist();


                                    Log.d("USBReceover", "exist:" + exist);
                                    if (exist) {
                                        File file = new File(PATH_SD);
                                        if(!file.exists()){
                                            continue;
                                        }
                                        Thread.sleep(2000);
                                        isSearch =false;
                                        if (!intent.getExtras().getBoolean("userStatus", false)) {
                                            Log.d("USBReceover", "run: 1");
                                            if(homePagerActivity!=null) {
                                                homePagerActivity.getScanService().ScanVideoMusic(context, 0);
                                            }else {
                                                isSearch =true;
                                            }
                                        }
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
                break;
            case Intent.ACTION_MEDIA_UNMOUNTED:
                break;
            case Intent.ACTION_MEDIA_EJECT:


                Toast.makeText(context, R.string.设备移除, Toast.LENGTH_SHORT).show();
                if(homePagerActivity!=null) {
                    homePagerActivity.getScanService().ScanVideoMusic(context, 0);
                }

                isSDOuting =true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            isSDOuting=false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case "android.hardware.usb.action.USB_DEVICE_ATTACHED":
                Toast.makeText(context, R.string.设备接入, Toast.LENGTH_SHORT).show();
                break;
            case "android.hardware.usb.action.USB_DEVICE_DETACHED":
                Toast.makeText(context, R.string.设备移除, Toast.LENGTH_SHORT).show();
                break;
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
                            (a[1].contains("extsd"))) {
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

    public SDBroadcastReceiver(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
        addReceiver();
        //startService(mService);
    }
    public void addReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);

        iFilter.addDataScheme("file");

        homePagerActivity.registerReceiver(this, iFilter);
    }
    }
