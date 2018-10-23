package com.kandi.dell.nscarlauncher.ui.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kandi.dell.nscarlauncher.ui.application.AppFragment;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;

public class USBBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
//            Toast.makeText(context, R.string.USB接入, Toast.LENGTH_SHORT).show();
           DialogLocalMusic.usbStatus = true;
           if(intent.getExtras().getBoolean("userStatus",false)){
               DialogLocalMusic.ScanVideoMusic(context,0);
           }


        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
//            Toast.makeText(context, R.string.USB拔出, Toast.LENGTH_SHORT).show();
//            DialogLocalMusic.updateGallery(context);
//            MusicFragment.reSetMusic(false);
//            VideoFragment.dialogLocalMusic.ScanVideo(context,false);

//            VideoFragment.dialogLocalMusic.ScanVideoMusic(context,0);
        }
        else  if(action.equals("android.intent.action.PACKAGE_ADDED")){
            AppFragment.refreshAppInfo();
                if(FragmentType.APPLICATION== HomePagerActivity.mCurFragment.getmType()){

                }
        }else if(action.equals("com.kangdi.home.hide")){
            HomePagerActivity.hideFragment();
        }

    }

    }
