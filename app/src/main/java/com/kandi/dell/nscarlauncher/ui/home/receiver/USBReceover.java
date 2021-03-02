package com.kandi.dell.nscarlauncher.ui.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.db.dao.MusicCollectionDao;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity.homePagerActivity;

public class USBReceover  extends BroadcastReceiver {
    public static final String PATH_SD = "/mnt/media_rw/extsd/";
    public static final String PATH_USB = "/mnt/media_rw/udisk/";
    boolean isSearch = false;
    public  Boolean  isSDOuting =false;//设备是否移除

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            Toast.makeText(context, R.string.USB接入, Toast.LENGTH_SHORT).show();
//            DialogLocalMusic.updateGallery(context);
//            DialogLocalMusic.updateGallery(context);
            new Thread(new Runnable() {
                int count =0;
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
                                    File file2 = new File(PATH_USB);
                                    if(!file.exists() && !file2.exists()){
                                        continue;
                                    }
                                    Thread.sleep(2000);
                                    homePagerActivity.getDialogLocalMusic().usbStatus = true;
                                    if(!intent.getExtras().getBoolean("userStatus",false)){
                                        homePagerActivity.getDialogLocalMusic().ScanVideoMusic(context,0);
                                    }
                                    isSearch =false;
                                    break;
                                }else {
                                    if (count>50){
                                        break;
                                    }
                                    count++;
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
            homePagerActivity.getDialogLocalMusic().usbStatus = false;
            homePagerActivity.getDialogLocalMusic().ScanVideoMusic(context,0);
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
            MusicCollectionDao.deleteFavByUsbOut(context,"/storage/udisk");
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
                            (a[1].contains("extsd") || a[1].contains("udisk"))) {
                        read.close();
                        return ret = true;
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
}
