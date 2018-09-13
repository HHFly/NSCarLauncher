package com.kandi.dell.nscarlauncher.ui.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.video.VideoFragment;

public class USBBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Toast.makeText(context, "USB接入", Toast.LENGTH_SHORT).show();
            MusicFragment.reSetMusic(true);
            VideoFragment.dialogLocalMusic.ScanVideo(context,true);


        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            Toast.makeText(context, "USB拔出", Toast.LENGTH_SHORT).show();
            MusicFragment.reSetMusic(false);
            VideoFragment.dialogLocalMusic.ScanVideo(context,false);
        }
    }

    }
