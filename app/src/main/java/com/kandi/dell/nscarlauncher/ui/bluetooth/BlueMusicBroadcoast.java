package com.kandi.dell.nscarlauncher.ui.bluetooth;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.common.util.LogUtils;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.phone.PhoneFragment;
import com.white.lib.utils.log.LogUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity.jumpFragment;

public class BlueMusicBroadcoast extends BroadcastReceiver {
    public final static int    PHONE_START               = 1;
    public final static int    PIN_CODE                  = 2;
    public final static int    PHONE_OUT                 = 3;
    public final static String TAG                       = "BlueMusicBroadcoast";
    public static       int    music_total_time          = 1;
    public final static String ACTION_REQUEST_PINCODE    = "com.kangdi.BroadCast.RequestPinCode";// 请求输入pin
    // code
    // 事件
    public final static String ACTION_PINCODE            = "com.kangdi.BroadCast.PinCode";// 输入的pin
    // code
    // 事件
    public final static String ACTION_RINGCALL           = "com.kangdi.BroadCast.RingCall";// 来电事件
    public final static String ACTION_CALLSTART          = "com.kangdi.BroadCast.CallStart";// 接通事件
    public final static String ACTION_CALLEND            = "com.kangdi.BroadCast.CallEnd";// 挂断事件
    public final static String ACTION_CALLOUT            = "com.kangdi.BroadCast.CallOutGoing";// 电话拨出事件
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
    static IKdBtService btservice = IKdBtService.Stub.asInterface(ServiceManager.getService("bt"));

    static AudioManager audioManager;
    private  String MusicStaus="0";
    @Override
    public void onReceive(Context context, Intent intent) {



        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.d(TAG, intent.getAction());

        if (intent.getAction().equals(ACTION_REQUEST_PINCODE)) {
            new Thread() {
                public void run() {
                    myHandler.sendMessage(myHandler.obtainMessage(PIN_CODE));
                }

                ;
            }.start();
        }
        if (intent.getAction().equals(ACTION_PBAP_GET_COMPLETED)) {
            new Thread() {
                public void run() {
                    try {
                        PhoneFragment.getPhoneBookStr(btservice.getContactsJsonString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ;
            }.start();
        }
        if (intent.getAction().equals(ACTION_PBAP_GET_CALL_HISTORY_COMPLETED)) {
            new Thread() {
                public void run() {
                    try {
                        PhoneFragment.getPhoneRecord();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ;
            }.start();
        }
        LogUtils.log("BlueMusicBroadcoast --> action: "+ intent.getAction());
        if (intent.getAction().equals(ACTION_HC)) {
            FlagProperty.flag_bluetooth = true;
//            KLog.e("flag_bluetooth: "+FlagProperty.flag_bluetooth);
//            if (HomePagerActivity.fragment_now instanceof FragmentPhone) {
//                HomePagerActivity.changeFragment(MainKondi.FRAGMENT_PHONE);
//            }
            HomePagerActivity.initBluetooth();

            PhoneFragment.setNullViewGone(false);
            PhoneFragment.getPhoneBook();
            PhoneFragment.getPhoneRecord();
        }
        if (intent.getAction().equals(ACTION_HD)) {
//            if (MainKondi.fragment_now instanceof FragmentPhone) {
//                MainKondi.changeFragment(MainKondi.FRAGMENT_PHONE);
//            }
            /*if (FragmentMusic.flag_bluetooth_music) {
                if (FragmentMusic.bt_open != null) {
                    FragmentMusic.bt_open.performClick();
                    FragmentMusic.flag_bluetooth_music = false;
                }

            }*/

            PhoneFragment.setNullViewGone(true);
            FlagProperty.flag_bluetooth = false;
            HomePagerActivity.initBluetooth();
        }
        if (intent.getAction().equals(ACTION_CALLSTART)) {
            int index = intent.getIntExtra(KEY_CALLINDEX, 0);
            Log.d("calltest", "call start");
            if (index == 1) {
                Log.d("calltest", "index = 1");
                FlagProperty.is_calling = true;
                FlagProperty.is_callindex_one = true;
                if (!FlagProperty.is_one_oper) {
                    Log.d("calltest", "no outgoing or ring, requestaudiofocus");
                    if (audioManager.requestAudioFocus(PhoneFragment.afChangeListener, 11,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    }
                }
                if (FlagProperty.flag_phone_ringcall) { // 来电
                    if (FlagProperty.flag_phone_incall_click) { // 板接
                        PhoneFragment.phoneStart();
                        FlagProperty.flag_phone_incall_click = false;
                    } else { // 手接
                        Intent i = new Intent();
                        i.setAction("phone.isgone");// 发出自定义广播
                        context.sendBroadcast(i);
                        new CallThread().start(); // 电话页面转换
                    }
                } else {
                    String number = intent.getStringExtra(KEY_PHONENUM).trim();
                    if (number.compareTo("") != 0) {
                        Log.d(TAG, "号码为：" + number);
                    } else {
                        Log.d(TAG, "号码为空");
                    }
                    Log.d("calltest", "changfragment to phone");
                    new CallThread().start(); // 电话页面转换
                }
            } else if (index == 2) {
                FlagProperty.is_callindex_two = true;
//                PhoneFragment.showKeepCall(FlagProperty.phone_number);
//                if (FlagProperty.flag_phone_ringcall) { // 来电
//                    if (FlagProperty.flag_phone_incall_click) { // 板接
//                        FlagProperty.flag_phone_incall_click = false;
//                    } else { // 手接
//                        Intent i = new Intent();
//                        i.setAction("phone.isgone");// 发出自定义广播
//                        context.sendBroadcast(i);
//                        new CallThread().start(); // 电话页面转换
//                    }
//                }
            }

        }
        if (intent.getAction().equals(ACTION_CALLEND)) {
            System.out.println("index:" + intent.getIntExtra(KEY_CALLINDEX, 0));
            int index = intent.getIntExtra(KEY_CALLINDEX, 0);
            if (index == 1) {
                FlagProperty.is_callindex_one = false;
                if (FlagProperty.is_callindex_two) {
                    PhoneFragment.hideThirdCallShow(2);
                }
            } else if (index == 2) {
                FlagProperty.is_callindex_two = false;
                if (FlagProperty.is_callindex_one) {
                    PhoneFragment.hideThirdCallShow(1);
                }
            }
            if (!FlagProperty.is_callindex_one && !FlagProperty.is_callindex_two) {
                FlagProperty.is_one_oper = false;
                FlagProperty.is_calling = false;
                PhoneFragment.flag_phone = false;
            }
            if (FlagProperty.flag_phone_ringcall) { // 来电时挂电话情况
                Intent i = new Intent();
                i.setAction("phone.isgone");// 发出自定义广播
                context.sendBroadcast(i);
                FlagProperty.flag_phone_ringcall = false; // 拨打电话结束情况
                if (!FlagProperty.is_callindex_one && !FlagProperty.is_callindex_two) {
                    Log.d("kondi", "BtPhone abandon audioFocus");
                    PhoneFragment.phoneStop(context);
                    if (audioManager.abandonAudioFocus(PhoneFragment.afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    }
                }
            } else {
                if (!FlagProperty.is_callindex_one && !FlagProperty.is_callindex_two) {
                    PhoneFragment.phoneStop(context);
                }
            }
        }
        if (intent.getAction().equals(ACTION_RINGCALL)) {

//            System.out.println("index:" + intent.getIntExtra(KEY_CALLINDEX, 0));
            try {
                FlagProperty.is_one_oper = true;
                if (!FlagProperty.flag_phone_ringcall) {
                    FlagProperty.flag_phone_ringcall = true;
                    String num = intent.getStringExtra(KEY_PHONENUM).trim();
                    int index =intent.getIntExtra(KEY_CALLINDEX, 0);
                    Intent i = new Intent();
                    i.setAction("phone.iscoming");// 发出自定义广播
                    i.putExtra("number", num);
                    i.putExtra("index", index);
                    context.sendBroadcast(i);
                    if(2!=index) {
                        FlagProperty.flag_phone_ringcall = true;
                        FlagProperty.phone_number = num; // 记录电话号码
                        FlagProperty.phone_number_one = num;
                    }
                }
            }catch (Exception e){

            }


        }
        if (intent.getAction().equals(ACTION_CALLACTIVE)) {
            int index = intent.getIntExtra(KEY_CALLINDEX, 0);
            System.out.println("index:" + intent.getIntExtra(KEY_CALLINDEX, 0));
            if (FlagProperty.is_callindex_one && FlagProperty.is_callindex_two) {
                PhoneFragment.changeCallInPhone(index);
            }
        }
        if (intent.getAction().equals(ACTION_CALLOUT)) {
            if(isHome(context)) {
                FlagProperty.is_one_oper = true;
                FlagProperty.phone_number = intent.getStringExtra(KEY_PHONENUM).trim(); // 记录电话号码
                FlagProperty.phone_number_one = FlagProperty.phone_number;
                new Thread() {
                    public void run() {
                        if (audioManager.requestAudioFocus(PhoneFragment.afChangeListener, 11,
                                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            Log.d("kondi", "BtPhone get AudioFocus");
                            //MainKondi.changeFragment(MainKondi.FRAGMENT_PHONE); // 拨打时时进入电话页面
                            jumpFragment(FragmentType.PHONE);
                            FlagProperty.flag_phone_incall_click = true;
                            myHandler.sendMessage(myHandler.obtainMessage(PHONE_OUT));
                            Log.d("kondi", "BtPhone change to PhonePage");
                        }
                    }

                }.start();
            }
        }
        if (intent.getAction().equals(ACTION_AC)) {
            // Toast.makeText(context, "音频蓝牙已连接", Toast.LENGTH_LONG);
            BTMusicFragment.setNullViewGone(false);
        }
        if (intent.getAction().equals(ACTION_AD)) {
            // Toast.makeText(context, "音频蓝牙已断开", Toast.LENGTH_LONG);
            BTMusicFragment.setNullViewGone(true);
            App.pagerOneHnadler.sendEmptyMessage(HandleKey.BTMUSICCOLSE);
        }
        if (intent.getAction().equals(ACTION_MUSIC_INFO_CHANGED)) {
            String music_info = intent.getStringExtra(KEY_MUSICINFO);
            LogUtils.log("BT:"+ACTION_MUSIC_INFO_CHANGED+":     "+music_info);
            try {
                JSONArray jsonArr = new JSONArray(music_info);
                JSONObject obj = (JSONObject) jsonArr.get(0);
                //if (FragmentMusic.flag_bluetooth_music) {
//                    MusicFragment.setMusicInfo(obj.getString("SongName"), obj.getString("SingerName"));
                    BTMusicFragment.setMusicInfo(obj.getString("SongName"), obj.getString("SingerName"));

                    if (obj.getString("SongTotalTime").compareTo("") == 0) {

                    } else {
//                        music_total_time = Integer.parseInt(obj.getString("SongTotalTime"));
//                        music_total_time /= 1000;
                        // FragmentMusic.music_total_time1
                        // .setText(getTime(music_total_time / 60) + ":" +
                        // getTime(music_total_time % 60));
                    }

                //}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (intent.getAction().equals(ACTION_BT_STREAM_START)) {
            LogUtils.log("BT:"+ACTION_BT_STREAM_START);
//            int  intent.getStringExtra(KEY_MEDIA_STATUS)
            App.get().PauseServiceFMMUSic();
            App.pagerOneHnadler.sendMessage(App.pagerOneHnadler.obtainMessage(HandleKey.BTMUSICOPEN));
            BTMusicFragment.myHandler.sendMessage(BTMusicFragment.myHandler.obtainMessage(11));
//            BTMusicFragment.isPlay=true;
//            BTMusicFragment.gifPlayShow();
//            HomePagerOneFragment.btPaly.setPlay(true);
            MusicFragment.stopView();

        }
        if (intent.getAction().equals(ACTION_BT_STREAM_SUSPEND)) {
//            int  intent.getStringExtra(KEY_MEDIA_STATUS)

            LogUtils.log("BT:"+ ACTION_BT_STREAM_SUSPEND);
//            BTMusicFragment.isPlay=false;
//            BTMusicFragment.gifPlayShow();
//            HomePagerOneFragment.btPaly.setPlay(false);
            BTMusicFragment.myHandler.sendMessage(BTMusicFragment.myHandler.obtainMessage(12));

            App.pagerOneHnadler.sendMessage(App.pagerOneHnadler.obtainMessage(HandleKey.BTMUSICCOLSE));
        }
        if (intent.getAction().equals(ACTION_TRACK_PROGRESS)) {

//                int time = intent.getIntExtra(KEY_TRACK_POSITON, 0);
//                BTMusicFragment.setBlueMusicProgress(time);

        }
        if (intent.getAction().equals(ACTION_SIM_CALL_START)) {
            FlagProperty.is_3gcall_start = true;
            FlagProperty.is_3g = true;
//            FragmentService.phoneStart();
        }
        // 3G拨号
        // if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
/*		if (intent.getAction().equals(ACTION_4G_OUT_GOING)) {
            FlagProperty.is_3g = true;
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Log.d("3gtest", "拨出电话:" + phoneNumber);
			if (audioManager.requestAudioFocus(FragmentService.afChangeListener, 10,
					AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

			}
		} */
        if(intent.getAction().equals(ACTION_MUSIC_CURRENT_POSITION)){
            int current = intent.getIntExtra("current",0);
            music_total_time =intent.getIntExtra("total",0);
            BTMusicFragment.setBlueMusicProgress(current);
//            LogUtils.log("BT:"+"current:"+current+"total"+ music_total_time);
//            if(!BTMusicFragment.isPlay){
//                BTMusicFragment.isPlay=true;
////                App.pagerOneHnadler.sendEmptyMessage(HandleKey.BTMUSICOPEN);
////                BTMusicFragment.myHandler.sendEmptyMessage(5);
//            }
        }

        if (intent.getAction().equals("com.kangdi.BroadCast.PhoneState")) {
            if (FlagProperty.is_3g) {
                FlagProperty.is_3g = false;
                if (FlagProperty.is_3gphone_comming) {
//                    if (audioManager.abandonAudioFocus(FragmentService.afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                        FlagProperty.is_3gphone_comming = false;
//                    }
                } else if (FlagProperty.is_3gcall_start) {
//                    FragmentService.phoneStop();
                    FlagProperty.is_3gcall_start = false;
                }
            }
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Intent i = new Intent();
                    i.setAction("3gphone.iscoming");// 发出自定义广播
                    i.putExtra("number", intent.getStringExtra("incoming_number"));
                    context.sendBroadcast(i);
                    FlagProperty.is_3gphone_comming = true;
                    FlagProperty.is_3g = true;
                    FlagProperty.phone_number = intent.getStringExtra("incoming_number");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    break;

                case TelephonyManager.CALL_STATE_IDLE:

                    break;
            }
        }
//       三方来电
        if(intent.getAction().equals(ACTION_CALL_TRIPARTITE_COMMING)){
            String numtwo  = intent.getStringExtra(KEY_PHONENUM_TRIPARTITE);
            FlagProperty.phone_number_two =numtwo;
            PhoneFragment.showCalling(numtwo);
        }
    // 挂断
        if(intent.getAction().equals(ACTION_CALL_TRIPARTITE_HANGUP)){
            PhoneFragment.showCallhangup();
        }
        //接听
        if(intent.getAction().equals(ACTION_CALL_TRIPARTITE_TALKING)){
//            PhoneFragment.showCallhAnswer();
        }
//        保持
        if(intent.getAction().equals(ACTION_CALL_TRIPARTITE_HANGON)){
//            PhoneFragment.showCallhKeep();
        }

    }
    private  static String getBtPlayStaus(boolean isPlay){

        return isPlay?"1":"0";
    }
    // 时间格式化为00
    public static String getTime(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }

    public static class CallThread extends Thread {
        @Override
        public void run() {
            //MainKondi.changeFragment(MainKondi.FRAGMENT_PHONE); // 拨打时时进入电话页面
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
    public static Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PHONE_START:
                    PhoneFragment.phoneStart();
                    break;
                case PIN_CODE:
                    try {
                        btservice.btPinCode(FlagProperty.BtCode);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case PHONE_OUT:
                    PhoneFragment.phoneCall(FlagProperty.phone_number);
                    Log.d("kondi", "BtPhone show Callout");
                    break;
                default:
                    break;
            }
        }

        ;
    };

}
