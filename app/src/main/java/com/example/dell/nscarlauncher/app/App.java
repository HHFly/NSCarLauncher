package com.example.dell.nscarlauncher.app;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IFmService;
import android.os.IKdAudioControlService;
import android.os.IKdBtService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.multidex.MultiDexApplication;

import com.example.dell.nscarlauncher.BuildConfig;
import com.example.dell.nscarlauncher.common.util.FrescoUtils;
import com.example.dell.nscarlauncher.common.util.PDLifecycleHandle;
import com.example.dell.nscarlauncher.common.util.ToastUtils;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.white.lib.utils.UtilsConfig;


/**
 * Created by z on 2018/4/10.
 */

public class
App extends MultiDexApplication {

    private static App s_app;

    public static App get() {
        return s_app;
    }

    private  IFmService radio;  //收音机

    private   IKdAudioControlService audioservice = IKdAudioControlService.Stub
            .asInterface(ServiceManager.getService("audioCtrl"));
    AudioManager audioManager;
    private IKdBtService btservice ;
    private Activity mCurActivity;

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

    //共享handle变量
    public static HomePagerOneFragment.PagerOneHnadler pagerOneHnadler;

    @Override
    public void onCreate() {
        super.onCreate();

        s_app = this;
        //初始化服务

        ToastUtils.init(this);
        registerActivityLifecycleCallbacks(new PDLifecycleHandle());
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
        initFm();
        btService();
    }
    /*蓝牙*/
    private  void  btService(){
        btservice = IKdBtService.Stub.asInterface(ServiceManager.getService("bt"));
    }
    /*收音机*/
    private void  initFm(){
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // radio初始化
        radio = IFmService.Stub.asInterface(ServiceManager.getService("fm"));
        try {

            radio.FmTest();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            audioservice.setBassLevel(120);
            audioservice.setSurroundWidth(7);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                mCurActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 获取当前Activity
     * @param <T>
     * @return
     */
    public <T extends Activity> T getCurrentActivity() {
        return (T) mCurActivity;
    }

    /**
     * 获取当前Activity的fragmentManager
     * @return
     */
    public FragmentManager getFragmentManager() {
        if (mCurActivity == null) {
            throw new NullPointerException("mCurActivity is null");
        }
        return mCurActivity.getFragmentManager();
    }




    /**
     * 获取当前Activity
     *
     * @return
     */
    public Activity getCurActivity() {
        return PDLifecycleHandle.currentActivity();
    }
}
