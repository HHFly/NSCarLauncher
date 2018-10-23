package com.kandi.dell.nscarlauncher.ui.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;

public class USBReceover  extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            Toast.makeText(context, R.string.USB接入, Toast.LENGTH_SHORT).show();
//            DialogLocalMusic.updateGallery(context);
//            DialogLocalMusic.updateGallery(context);

        }
        else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            Toast.makeText(context, R.string.USB拔出, Toast.LENGTH_SHORT).show();
            DialogLocalMusic.usbStatus = false;
            DialogLocalMusic.ScanVideoMusic(context,0);
        }
    }
}
