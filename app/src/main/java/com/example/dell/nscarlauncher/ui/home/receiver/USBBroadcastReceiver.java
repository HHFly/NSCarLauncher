package com.example.dell.nscarlauncher.ui.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.nscarlauncher.ui.music.fragment.MusicFragment;

public class USBBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                MusicFragment.reSetMusic(true);

        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            MusicFragment.reSetMusic(false);
        }
    }

    }
