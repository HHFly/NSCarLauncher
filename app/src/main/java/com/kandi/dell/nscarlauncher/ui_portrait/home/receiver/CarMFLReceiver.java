package com.kandi.dell.nscarlauncher.ui_portrait.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.common.util.LogUtils;
import com.kandi.dell.nscarlauncher.common.util.ToastUtils;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;

import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.music.model.MusicModel;
import com.kandi.dell.nscarlauncher.ui_portrait.fm.FMFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;


public class CarMFLReceiver extends BroadcastReceiver {
    public static  final String ACTION_WHEEL_MODE ="com.kangdi.BroadCast.WheelMode";//多功能模式
    public static final String ACTION_WHEEL_VOICE ="com.kangdi.BroadCast.WheelVoice";//语音
    public static final String ACTION_WHEEL_MUSIC_PREV ="com.kangdi.BroadCast.WheelMusicPrev";//上一首
    public static final String ACTION_WHEEL_MUSIC_NEXT="com.kangdi.BroadCast.WheelMusicNext";//下一首
    public static  final String ACTION_WHEEL_CALL="com.kangdi.BroadCast.WheelCall";//接听
    public static  final String ACTION_WHEEL_HANGUP="com.kangdi.BroadCast.WheelHangup";//挂断

    private @FragmentType int nowFragment=FragmentType.FM;//模式切换
    private boolean isMute;//是否静音
    private int BlueVolume =7;
    HomePagerActivity homePagerActivity;
     @Override
    public void onReceive(Context context, Intent intent) {
        //多功能模式切换
        if (intent.getAction().equals(ACTION_WHEEL_MODE)) {
            LogUtils.log(ACTION_WHEEL_MODE);
            if( SystemProperties.getInt("sys.kd.revers",0)==0) {
                homePagerActivity.jumpFragment(nowFragment);
              switch (nowFragment){
                  case FragmentType.FM:
                      nowFragment=FragmentType.BTMUSIC;
                      break;
                  case FragmentType.BTMUSIC:
                      nowFragment=FragmentType.MUSIC;
                    break;
                  case  FragmentType.MUSIC:
                      nowFragment=FragmentType.PHONE;
                      break;
                  case FragmentType.PHONE:
                      nowFragment=FragmentType.VIDEO;
                  case FragmentType.VIDEO:
                      nowFragment =FragmentType.FM;
                      break;
              }

            }
        }
        //语音
        if (intent.getAction().equals(ACTION_WHEEL_VOICE)) {
            LogUtils.log(ACTION_WHEEL_VOICE);
            if( SystemProperties.getInt("sys.kd.revers",0)==0) {

            }
        }
        //上一首
        if (intent.getAction().equals(ACTION_WHEEL_MUSIC_PREV)) {
            LogUtils.log(ACTION_WHEEL_MUSIC_PREV);
            if( SystemProperties.getInt("sys.kd.revers",0)==0) {
               if(homePagerActivity.getBtMusicFragment().isPlay){
                   homePagerActivity.getBtMusicFragment().myHandler.sendEmptyMessage(6);
               }
               if(homePagerActivity.getMusicFragment().flag_play){
                   if( homePagerActivity.getDialogLocalMusicD().data.size()>0) {


                       MusicModel.getPrevMusic(context, homePagerActivity.getMusicFragment().music_model);
                   }
               }
               if(homePagerActivity.getFmFragment().isPlay){
                   homePagerActivity.getFmFragment().myHandler.sendEmptyMessage(FMFragment.LAST);
               }
            }
        }
        //下一首
        if (intent.getAction().equals(ACTION_WHEEL_MUSIC_NEXT)) {
            LogUtils.log(ACTION_WHEEL_MUSIC_NEXT);
            if( SystemProperties.getInt("sys.kd.revers",0)==0) {
                if(homePagerActivity.getBtMusicFragment().isPlay){
                    homePagerActivity.getBtMusicFragment().myHandler.sendEmptyMessage(7);
                }
                if(homePagerActivity.getMusicFragment().flag_play){
                    if(homePagerActivity.getDialogLocalMusicD().data.size()>0) {


                        MusicModel.getNextMusic(context,  homePagerActivity.getMusicFragment().music_model);
                    }
                }
                if(homePagerActivity.getFmFragment().isPlay){
                    homePagerActivity.getFmFragment().myHandler.sendEmptyMessage(FMFragment.NEXT);
                }
            }
        }
        //接听
        if (intent.getAction().equals(ACTION_WHEEL_CALL)) {
            if( FlagProperty.flag_phone_ringcall ) {
                FlagProperty.flag_phone_ringcall=false;
                LogUtils.log(ACTION_WHEEL_CALL);
                homePagerActivity.jumpFragment(FragmentType.PHONE);
                FlagProperty.flag_phone_incall_click = true;
                homePagerActivity.getPhoneFragment().getpPhoneFragment().answerPhone();
//               homePagerActivity.getPhoneFragment().phoneStart();
            }

        }
        //挂断
        if (intent.getAction().equals(ACTION_WHEEL_HANGUP)) {
            LogUtils.log(ACTION_WHEEL_HANGUP);
            FlagProperty.flag_phone_ringcall = false;
            homePagerActivity.getPhoneFragment().getpPhoneFragment().hangDownphone();
        }
        //app安装
         if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
             App.get().getCurActivity().getAppFragment().refreshAppInfo();
             if (FragmentType.APPLICATION == homePagerActivity.mCurFragment.getmType()) {

             }
         }

         if(intent.getAction().equals("com.kangdi.home.hide")){
             homePagerActivity.hideFragment();
         }

//         if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
//             int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
//             if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
//                 if(App.get().getCurActivity().getSetFragment().getWifiFragment() != null && !App.get().getCurActivity().getSetFragment().getWifiFragment().error){
//                     App.get().getCurActivity().getSetFragment().getWifiFragment().error = true;
//                     ToastUtils.show(context,context.getString(R.string.Wifi密码错误));
//                 }
//             }
//         }

    }

    public CarMFLReceiver() {
    }

    public CarMFLReceiver(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
        addReceiver();
    }
    public void addReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_MODE);
        intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_VOICE);
        intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_MUSIC_PREV);
        intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_MUSIC_NEXT);
        intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_CALL);
        intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_HANGUP);
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("com.kangdi.home.hide");
//        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        homePagerActivity.registerReceiver(this, intentFilter);
    }
}
