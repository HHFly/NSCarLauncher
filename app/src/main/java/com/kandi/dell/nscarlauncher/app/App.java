package com.kandi.dell.nscarlauncher.app;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IFmService;
import android.os.IKdAudioControlService;
import android.os.IKdBtService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.support.multidex.MultiDexApplication;
import android.widget.FrameLayout;

import com.kandi.dell.nscarlauncher.BuildConfig;
import com.kandi.dell.nscarlauncher.base.Activity.BaseActivity;
import com.kandi.dell.nscarlauncher.common.util.FrescoUtils;
import com.kandi.dell.nscarlauncher.common.util.JsonUtils;
import com.kandi.dell.nscarlauncher.common.util.NSLifecycleHandle;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.common.util.TimeUtils;
import com.kandi.dell.nscarlauncher.common.util.ToastUtils;
import com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
import com.white.lib.utils.UtilsConfig;

import static com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast.ACTION_CALLEND;
import static com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast.ACTION_CALLOUT;
import static com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast.ACTION_CALLSTART;
import static com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast.ACTION_RINGCALL;
import static com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty.PAUSE_MSG;
import static com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment.broadcastMusicInfo;


/**
 * Created by z on 2018/4/10.
 */

public class
App extends MultiDexApplication {

    private static App s_app;

    public static App get() {
        return s_app;
    }
    private  FrameLayout frameLayout;//主界面
    private  IFmService radio;  //收音机
    private MediaPlayer mediaPlayer;//本地音乐播放
    private   IKdAudioControlService audioservice = IKdAudioControlService.Stub
            .asInterface(ServiceManager.getService("audioCtrl"));
    AudioManager audioManager;
    private IKdBtService btservice ;
    private BaseActivity mCurActivity;
    private BlueMusicBroadcoast bluetoothReceiver; //蓝牙广播接受
    private Equalizer mEqualizer;

    public  IFmService getRadio() {
        return radio;
    }

    public  IKdAudioControlService getAudioservice() {
        return audioservice;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public IKdBtService getBtservice() {
        return btservice;
    }

    public Equalizer getmEqualizer() {
        try {
            mEqualizer.getBandLevel((short) 0);
            return mEqualizer;
        }catch (Exception e){
            mEqualizer=null;
            mEqualizer = new Equalizer(0, App.get().getMediaPlayer().getAudioSessionId());
            mEqualizer.setEnabled(true);
            int postion=  SPUtil.getInstance(this,"EQ").getInt("EQPosition", 1);
            if(0==postion) {
                String set = SPUtil.getInstance(this, "EQ").getString("EQSet");
                if (set != null) {
                    Equalizer.Settings settings = JsonUtils.fromJson(set, Equalizer.Settings.class);
                    if (settings != null) {
                        try {
                            mEqualizer.setProperties(settings);
                        }catch (Exception ex){

                        }

                    }
                }
            }else {
                try {
                    mEqualizer.usePreset((short) (postion-1));
                }catch (Exception e1){

                }

            }
            return mEqualizer;
        }


    }

    //共享handle变量
    public static HomePagerOneFragment.PagerOneHnadler pagerOneHnadler;

    @Override
    public void onCreate() {
        super.onCreate();

        s_app = this;
        //初始化服务
        SystemProperties.set("sys.nushine.k28_application", BuildConfig.VERSION_NAME);

        ToastUtils.init(this);
        registerActivityLifecycleCallbacks(new NSLifecycleHandle());
        //初始化Fresco
        FrescoUtils.initialize(this);
        //初始化white框架
        UtilsConfig
                .getInstance(this)
                .setLogOpen(BuildConfig.IS_OPEN_LOG);
        /*初始化Handle*/
        pagerOneHnadler = new HomePagerOneFragment.PagerOneHnadler();


        /*初始化驱动模块*/
        initService();

    }
    private void initService(){
       initMusic();
        initFm();
        btService();
        registMyReceiver();
    }
    public void reSetMusic(){
        mediaPlayer.release();
        mediaPlayer=null;
        mEqualizer.release();
        mEqualizer=null;
        initMusic();
    }
/*音乐均衡器*/
    private void initMusic() {
        mediaPlayer= new MediaPlayer();
        mEqualizer = new Equalizer(0, App.get().getMediaPlayer().getAudioSessionId());
        mEqualizer.setEnabled(true);
        int postion=  SPUtil.getInstance(this,"EQ").getInt("EQPosition", 1);
        if(0==postion) {
            String set = SPUtil.getInstance(this, "EQ").getString("EQSet");
            if (set != null) {
                Equalizer.Settings settings = JsonUtils.fromJson(set, Equalizer.Settings.class);
                if (settings != null) {
                    try {
                        mEqualizer.setProperties(settings);
                    }catch (Exception e){

                    }

                }
            }
        }else {
            try {
                mEqualizer.usePreset((short) (postion-1));
            }catch (Exception e){}

        }
    }

    /*蓝牙*/
    private  void  btService(){
        try {
            btservice = IKdBtService.Stub.asInterface(ServiceManager.getService("bt"));
        }catch (Exception e){

        }

    }
    /*收音机*/
    private void  initFm(){
        try {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // radio初始化
        radio = IFmService.Stub.asInterface(ServiceManager.getService("fm"));

            audioservice.setBassLevel(120);
            audioservice.setSurroundWidth(7);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /*反注册接收器*/
    public void  unregistMyReceiver(){
        try {
            if (bluetoothReceiver!=null) {
                this.unregisterReceiver(bluetoothReceiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /*注册接收器*/
    private void registMyReceiver() {
        bluetoothReceiver = new BlueMusicBroadcoast();
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
        registerReceiver(bluetoothReceiver, intentFilter);
    }



    /**
     * 获取当前Activity
     * @param <T>
     * @return
     */
    public <T extends Activity> T getCurrentActivity() {
        return (T) mCurActivity;
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 获取当前Activity
     *
     * @return
     */
    public BaseActivity getCurActivity() {
        return mCurActivity;
    }
    public void  setmCurActivity(BaseActivity curActivity){
        this.mCurActivity =curActivity;
    }

    public void PauseService(){
        try {

            radio.CloseLocalRadio();
            btservice.btAvrPause();
            broadcastMusicInfo(getApplicationContext(), PAUSE_MSG);
            HomePagerTwoFragment.myHandler.sendEmptyMessage(1);
            pagerOneHnadler.sendEmptyMessage(HandleKey.FM);
            pagerOneHnadler.sendEmptyMessage(HandleKey.BTMUSICCOLSE);
            mediaPlayer.release();
            mEqualizer.release();
            mediaPlayer = null;

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void PauseServiceFMMUSic(){
        try {
            radio.CloseLocalRadio();
            broadcastMusicInfo(getApplicationContext(), PAUSE_MSG);
            HomePagerTwoFragment.myHandler.sendEmptyMessage(1);
            pagerOneHnadler.sendEmptyMessage(HandleKey.FM);
//            pagerOneHnadler.sendEmptyMessage(HandleKey.BTMUSIC);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void PauseServiceFMBTMUSic(){
        try {
            radio.CloseLocalRadio();
            btservice.btAvrPause();
            pagerOneHnadler.sendEmptyMessage(HandleKey.FM);
            pagerOneHnadler.sendEmptyMessage(HandleKey.BTMUSICCOLSE);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void PauseServiceMUSic(){
        try {
            broadcastMusicInfo(getApplicationContext(), PAUSE_MSG);
            btservice.btAvrPause();
            HomePagerTwoFragment.myHandler.sendEmptyMessage(1);
            pagerOneHnadler.sendEmptyMessage(HandleKey.BTMUSICCOLSE);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
