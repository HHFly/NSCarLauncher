package com.kandi.dell.nscarlauncher.ui_portrait.home.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast.ACTION_CALLEND;
import static com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast.ACTION_CALLOUT;
import static com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast.ACTION_CALLSTART;
import static com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast.ACTION_RINGCALL;

public class BTBroadcoastReceiver extends BroadcastReceiver {
    public final static int    PHONE_START               = 1;
    public final static int    PIN_CODE                  = 2;
    public final static int    PHONE_OUT                 = 3;
    public final static int    BTPhone_CHANGE           = 4;
    public final static int    BTMusic_CHANGE            =5;
    HomePagerActivity homePagerActivity;
    public final static String ACTION_REQUEST_PINCODE    = "com.kangdi.BroadCast.RequestPinCode";// 请求输入pin
    // code
    // 事件
    public final static String ACTION_PINCODE            = "com.kangdi.BroadCast.PinCode";// 输入的pin
    // code
    // 事件
    public final static String ACTION_RINGCALL           = "com.kangdi.BroadCast.RingCall.Launcher";// 来电事件
    public final static String ACTION_CALLSTART          = "com.kangdi.BroadCast.CallStart.Launcher";// 接通事件
    public final static String ACTION_CALLEND            = "com.kangdi.BroadCast.CallEnd.Launcher";// 挂断事件
    public final static String ACTION_CALLOUT            = "com.kangdi.BroadCast.CallOutGoing.Launcher";// 电话拨出事件
    public final static String ACTION_HC                 = "com.kangdi.BroadCast.HandsFreeConnect";// 手机蓝牙已连接
    public final static String ACTION_HD                 = "com.kangdi.BroadCast.HandsFreeDisconnect";// 手机蓝牙未连接
    public final static String ACTION_AC                 = "com.kangdi.BroadCast.AudioConnect";// 音频蓝牙已连接
    public final static String ACTION_AD                 = "com.kangdi.BroadCast.AudioDisconnect";// 音频蓝牙已断开
    public final static String ACTION_PBAP_CONNECT       = "com.kangdi.BroadCast.PbapConnect";// 请求获取电话本
    public final static String ACTION_PBAP_GET_COMPLETED = "com.kangdi.BroadCast.PbapGetCompleted";// 获取电话本完成
    public final static  String ACTION_PBAP_GET_CALL_HISTORY_COMPLETED ="com.kangdi.BroadCast.PbapGetCallHistoryCompleted";
    public final static String ACTION_SIM_CALL_START     = "com.kangdi.BroadCast.SimCallStart";// SIM卡接通广播
    public final static String ACTION_MUSIC_INFO_CHANGED = "com.kangdi.BroadCast.MusicInfoChanged";// 歌曲信息变更
    public final static String KEY_PHONENUM              = "com.kangdi.key.phonenum";// 电话号码的KEY
    public final static String KEY_MUSICINFO             = "com.kangdi.key.musicinfo";// 歌曲信息变更之后的发送广播的String数据的KEY

    public final static String ACTION_TRACK_PROGRESS = "com.kangdi.BroadCast.TrackProgress";// 蓝牙音乐进度变更
    public final static String KEY_TRACK_POSITON     = "com.kangdi.key.trackposition";

    public final static String KEY_CALLINDEX                        = "com.kangdi.key.callindex";// 来电通话索引
    public final static String ACTION_CALLACTIVE                    = "com.kangdi.BroadCast.CallActive";// 通话激活事件
    public final static String ACTION_AC_ENABLE_APP_SETTING_CHANGED = "com.kangdi.BroadCast.AcEnableAppSettingChanged";// 蓝牙音乐支持模式变更

    public final static String ACTION_4G_OUT_GOING = "com.kangdi.BroadCast.open4GAduioPath"; // 4g电话拨通

    public final static String ACTION_AC_MEDIA_STATUS_CHANGED = "com.kangdi.BroadCast.AcMediaStatusChanged";//蓝牙音乐开关广播
    public final static String   ACTION_BT_STREAM_SUSPEND= "com.kangdi.BroadCast.BtStreamSusppend"; //蓝牙音乐开关广播
    public final static String   ACTION_BT_STREAM_START= "com.kangdi.BroadCast.BtStreamStart";//蓝牙音乐kai广播
    public final static String   ACTION_MUSIC_CURRENT_POSITION = "com.kangdi.BroadCast.MusicCurrentPosition";//蓝牙音乐进度条

    public final static String   ACTION_CALL_TRIPARTITE_COMMING = "com.kangdi.BroadCast.tripartite.comming"; //三方来电ieonSecondTalking
    public final static String   ACTION_CALL_TRIPARTITE_HANGUP = "com.kangdi.BroadCast.tripartite.hangup";  //挂断
    public final static String   ACTION_CALL_TRIPARTITE_TALKING = "com.kangdi.BroadCast.tripartite.talking"; //接听
    public final static String   ACTION_CALL_TRIPARTITE_HANGON = "com.kangdi.BroadCast.tripartite.hangon"; //保持
    public final static String   KEY_PHONENUM_TRIPARTITE = "com.kangdi.key.tripartite.phonenum";//电话
    @Override
    public void onReceive(Context context, Intent intent) {
        String action =intent.getAction();
        switch (action){
            case ACTION_REQUEST_PINCODE:
                myHandler.sendMessage(myHandler.obtainMessage(PIN_CODE));
                break;
            case ACTION_PBAP_GET_COMPLETED:
                new Thread() {
                    public void run() {
                        try {
                            homePagerActivity.getPhoneInfoService().getPhoneBook();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
                break;
            case ACTION_PBAP_GET_CALL_HISTORY_COMPLETED:
                new Thread() {
                    public void run() {
                        try {
                            homePagerActivity.getPhoneInfoService().getPhoneRecord();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
                break;
            case ACTION_HC:
                FlagProperty.flag_bluetooth = true;
                myHandler.sendMessage(myHandler.obtainMessage(BTPhone_CHANGE));
                break;
            case ACTION_HD:
                FlagProperty.flag_bluetooth = false;
                myHandler.sendMessage(myHandler.obtainMessage(BTPhone_CHANGE));
                break;
            case ACTION_CALLSTART:
                CallStart(intent);
                break;
            case ACTION_CALLEND:
                CallEnd(intent,context);
                break;
            case ACTION_RINGCALL:
                CallRing(intent);
                break;
            case ACTION_CALLACTIVE:

                break;
            case ACTION_CALLOUT:
                CallingOut(intent,context);
                break;
            case ACTION_AC:
                myHandler.sendMessage(myHandler.obtainMessage(BTMusic_CHANGE));
                break;
            case ACTION_AD:
                myHandler.sendMessage(myHandler.obtainMessage(BTMusic_CHANGE));
                break;
            case ACTION_MUSIC_INFO_CHANGED:
                BtMusicInfo(intent);
                break;
            case ACTION_BT_STREAM_START:
                homePagerActivity.getBtMusicFragment().myHandler.sendMessage( homePagerActivity.getBtMusicFragment().myHandler.obtainMessage(11));
                break;
            case ACTION_BT_STREAM_SUSPEND:
                homePagerActivity.getBtMusicFragment().myHandler.sendMessage( homePagerActivity.getBtMusicFragment().myHandler.obtainMessage(12));
                break;
            case ACTION_MUSIC_CURRENT_POSITION:
                int current = intent.getIntExtra("current",0);
                int music_total_time =intent.getIntExtra("total",0);
                homePagerActivity.getBtMusicFragment().setMusicProgressHanle(current,music_total_time);
                break;
        }


    }

    private void BtMusicInfo(Intent intent){
        String music_info = intent.getStringExtra(KEY_MUSICINFO);

        try {
            JSONArray jsonArr = new JSONArray(music_info);
            JSONObject obj = (JSONObject) jsonArr.get(0);
            FlagProperty.SongName =obj.getString("SongName");
            FlagProperty.SingerName =obj.getString("SingerName");
            homePagerActivity.getBtMusicFragment().setMusicInfoHanle( FlagProperty.SongName, FlagProperty.SingerName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void CallingOut(Intent intent,Context context){
        if(isHome(context)) {
            FlagProperty.is_one_oper = true;
            FlagProperty.phone_number = intent.getStringExtra(KEY_PHONENUM).trim(); // 记录电话号码
            FlagProperty.phone_number_one = FlagProperty.phone_number;
            Log.d("MediaFocusControl", "====home====ACTION_CALLOUT=======");
            if (App.get().getAudioManager().requestAudioFocus(homePagerActivity.getPhoneFragment().getpPhoneFragment().afChangeListener, 22,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.d("kondi", "BtPhone get AudioFocus");
                //MainKondi.changeFragment(MainKondi.FRAGMENT_PHONE); // 拨打时时进入电话页面
                homePagerActivity.hideLoadingDialog();

                homePagerActivity.jumpFragment(FragmentType.PHONE);

                FlagProperty.flag_phone_incall_click = true;
                myHandler.sendMessage(myHandler.obtainMessage(PHONE_OUT));

            }
        }
    }
    private void CallRing(Intent intent){
        try {
//                int blueVolume =  (int) Math.round((float)30/100.0  * 21.0);
//                btservice.btSetVol(String.valueOf(blueVolume));
            FlagProperty.is_one_oper = true;
            if (!FlagProperty.flag_phone_ringcall) {
                FlagProperty.flag_phone_ringcall = true;
                String num = intent.getStringExtra(KEY_PHONENUM).trim();
                int index =intent.getIntExtra(KEY_CALLINDEX, 0);
                if(2!=index) {
                    FlagProperty.flag_phone_ringcall = true;
                    FlagProperty.phone_number = num; // 记录电话号码
                    FlagProperty.phone_number_one = num;
                    if (App.get().getAudioManager().requestAudioFocus(homePagerActivity.getPhoneFragment().getpPhoneFragment().afChangeListener, 22,
                            AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        homePagerActivity.hideLoadingDialog();
                        homePagerActivity.jumpFragment(FragmentType.PHONE);
                        homePagerActivity.getPhoneFragment().callIn(intent.getStringExtra(KEY_PHONENUM).trim(),intent.getStringExtra("address"),intent.getStringExtra("type"));

                    }
                }
            }
        }catch (Exception e){

        }
    }
    private void CallStart(Intent intent){
        int index =intent.getIntExtra(KEY_CALLINDEX, 0);
        if(index == 1){
            FlagProperty.is_calling = true;
            FlagProperty.is_callindex_one = true;
            if (!FlagProperty.is_one_oper) {

                if (App.get().getAudioManager().requestAudioFocus(homePagerActivity.getPhoneFragment().getpPhoneFragment().afChangeListener, 22,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                }
                if (FlagProperty.flag_phone_ringcall) { // 来电
                    if (FlagProperty.flag_phone_incall_click) { // 板接
                        new CallThread().start(); // 电话页面转换
                        FlagProperty.flag_phone_incall_click = false;
                    } else { // 手接

                        new CallThread().start(); // 电话页面转换
                    }
                } else {
                    String number = intent.getStringExtra(KEY_PHONENUM).trim();

                    new CallThread().start(); // 电话页面转换
                }

            }
        }else if (index == 2) {
            FlagProperty.is_callindex_two = true;


        }

    }
    private void CallEnd(Intent intent,Context context){
        int index = intent.getIntExtra(KEY_CALLINDEX, 0);
        Log.d("MediaFocusControl", "=====home==ACTION_CALLEND========"+index);
        if (index == 1) {
            FlagProperty.is_callindex_one = false;
            Log.d("MediaFocusControl", "=====home==ACTION_CALLEND========is_callindex_one-->"+FlagProperty.is_callindex_one);
            if (FlagProperty.is_callindex_two) {
//                    homePagerActivity.getPhoneFragment().hideThirdCallShow(2);
            }
        } else if (index == 2) {
            FlagProperty.is_callindex_two = false;
            Log.d("MediaFocusControl", "=====home==ACTION_CALLEND========is_callindex_two-->"+FlagProperty.is_callindex_two);
            if (FlagProperty.is_callindex_one) {
//                    homePagerActivity.getPhoneFragment().hideThirdCallShow(1);
            }
        }
        if (!FlagProperty.is_callindex_one && !FlagProperty.is_callindex_two) {
            FlagProperty.is_one_oper = false;
            FlagProperty.is_calling = false;
            homePagerActivity.getPhoneFragment().getpPhoneFragment().flag_phone = false;
        }
        if (FlagProperty.flag_phone_ringcall) { // 来电时挂电话情况
            Log.d("MediaFocusControl", "=====home==ACTION_CALLEND======11111111==");
            Intent i = new Intent();
            i.setAction("phone.isgone");// 发出自定义广播
            context.sendBroadcast(i);
            FlagProperty.flag_phone_ringcall = false; // 拨打电话结束情况
            if (!FlagProperty.is_callindex_one && !FlagProperty.is_callindex_two) {
                Log.d("kondi", "BtPhone abandon audioFocus");
                Log.d("MediaFocusControl", "BtPhone abandon audioFocus010");
                if (App.get().getAudioManager().abandonAudioFocus(homePagerActivity.getPhoneFragment().getpPhoneFragment().afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                }
                homePagerActivity.getPhoneFragment().phoneStop(context);
            }
        } else {
            Log.d("MediaFocusControl", "=====home==ACTION_CALLEND======2222222=="+FlagProperty.is_callindex_one + "     "+FlagProperty.is_callindex_two);
            if (!FlagProperty.is_callindex_one && !FlagProperty.is_callindex_two) {
                Log.d("MediaFocusControl", "===home===ACTION_CALLEND======");
                if (App.get().getAudioManager().abandonAudioFocus(homePagerActivity.getPhoneFragment().getpPhoneFragment().afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                }
                homePagerActivity.getPhoneFragment().phoneStop(context);
            }
        }
    }
    public  class CallThread extends Thread {
        @Override
        public void run() {

            myHandler.sendMessage(myHandler.obtainMessage(PHONE_START));
        }
    }
    /*判断是顶部app是否是桌面*/

    private boolean isHome(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        Log.d("SystemUI",rti.get(0).topActivity.getPackageName());
        return "com.kandi.nscarlauncher".equals(rti.get(0).topActivity.getPackageName());
    }
    public  Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BTMusic_CHANGE:
                    homePagerActivity.getBtMusicFragment().requestAudioFocus();
                    break;
                case BTPhone_CHANGE:
                    homePagerActivity.getPhoneFragment().getpPhoneFragment().requestAudioFocus();
                    break;
                case PHONE_START:
                  homePagerActivity.getPhoneFragment().phoneStart();
                    break;
                case PIN_CODE:
                    try {
                        App.get().getBtservice().btPinCode(FlagProperty.BtCode);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case PHONE_OUT:
                    homePagerActivity.getPhoneFragment().phoneCall(FlagProperty.phone_number);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public BTBroadcoastReceiver(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
        addReceiver();
    }
    public void addReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.kangdi.BroadCast.RequestPinCode");
        intentFilter.addAction("com.kangdi.BroadCast.PinCode");
        intentFilter.addAction(ACTION_RINGCALL);
        intentFilter.addAction(ACTION_CALLSTART);
        intentFilter.addAction(ACTION_CALLEND);
        intentFilter.addAction(ACTION_CALLOUT);
        intentFilter.addAction("com.kangdi.BroadCast.HandsFreeConnect");
        intentFilter.addAction("com.kangdi.BroadCast.HandsFreeDisconnect");
        intentFilter.addAction("com.kangdi.BroadCast.AudioConnect");
        intentFilter.addAction("com.kangdi.BroadCast.AudioDisconnect");
        intentFilter.addAction("com.kangdi.BroadCast.PbapConnect");
        intentFilter.addAction("com.kangdi.BroadCast.PbapGetCompleted");
        intentFilter.addAction("com.kangdi.BroadCast.SimCallStart");
        intentFilter.addAction("com.kangdi.BroadCast.MusicInfoChanged");
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("com.kangdi.BroadCast.TrackProgress");
        intentFilter.addAction("com.kangdi.BroadCast.CallActive");
        intentFilter.addAction("com.kangdi.BroadCast.BtStreamSusppend");
        intentFilter.addAction("com.kangdi.BroadCast.MusicCurrentPosition");
        intentFilter.addAction("com.kangdi.BroadCast.open4GAduioPath");
        intentFilter.addAction("com.kangdi.BroadCast.AcMediaStatusChanged");
        intentFilter.addAction("com.kangdi.BroadCast.PhoneState");
        intentFilter.addAction("com.kangdi.BroadCast.BtStreamStart");
//        三方通话
        intentFilter.addAction("com.kangdi.BroadCast.tripartite.hangon");
        intentFilter.addAction("com.kangdi.BroadCast.tripartite.talking");
        intentFilter.addAction("com.kangdi.BroadCast.tripartite.hangup");
        intentFilter.addAction("com.kangdi.BroadCast.tripartite.comming");
        intentFilter.addAction("com.kangdi.BroadCast.PbapGetCallHistoryCompleted");

        homePagerActivity.registerReceiver(this, intentFilter);
    }
}
