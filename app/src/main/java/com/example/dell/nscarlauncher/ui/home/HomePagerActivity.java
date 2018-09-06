package com.example.dell.nscarlauncher.ui.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.driverlayer.kdos_driverServer.IECarDriver;
import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.Activity.BaseActivity;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.FragmentUtils;
import com.example.dell.nscarlauncher.common.util.JumpUtils;
import com.example.dell.nscarlauncher.common.util.LogUtils;
import com.example.dell.nscarlauncher.common.util.NetUtils;
import com.example.dell.nscarlauncher.common.util.TimeUtils;
import com.example.dell.nscarlauncher.ui.application.AppFragment;
import com.example.dell.nscarlauncher.ui.bluetooth.BTMusicFragment;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.example.dell.nscarlauncher.ui.fm.FMFragment;
import com.example.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.example.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerThreeFragment;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
import com.example.dell.nscarlauncher.ui.home.model.HomeModel;
import com.example.dell.nscarlauncher.ui.home.receiver.USBBroadcastReceiver;
import com.example.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.example.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.example.dell.nscarlauncher.ui.phone.PhoneFragment;
import com.example.dell.nscarlauncher.ui.setting.SetFragment;
import com.example.dell.nscarlauncher.ui.video.VideoFragment;
import com.example.dell.nscarlauncher.widget.DialogVolumeControl;


import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.relex.circleindicator.CircleIndicator;


public class HomePagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    public static final int CALL_ANSWER = 4; // 接听来电
    public static final int CALL_HUNGUP = 5; // 挂断来电
    private static FrameLayout frameLayout;//主界面
   static HomePagerOneFragment homePagerOneFragment;
    public static int staus; //	1空调离线,0空调正常；
    HomePagerTwoFragment homePagerTwoFragment;
    HomePagerThreeFragment homePagerThreeFragment;
    private DialogVolumeControl dialogVolumeControl ;
    private ArrayList<Fragment> mFragments;
    private ViewPager viewPager;
    private  CircleIndicator indicator;//viewpager指示器
    public static BaseFragment mCurFragment;//当前页
    public static FMFragment fmFragment ;//收音机
    public static  BTMusicFragment btMusicFragment;//蓝牙音乐
    public static MusicFragment musicFragment;//本地音乐
    public static PhoneFragment phoneFragment;//电话
    public static SetFragment setFragment;//设置
    public  static AppFragment appFragment;//应用
    public  static VideoFragment videoFragment;//视频
    private  static  BaseActivity context;
    private ArrayList<HomeModel> mData;
    static Dialog alertDialog;//来电弹框
    public ComingReceiver comingReceiver;
    public USBBroadcastReceiver usbBroadcastReceiver;
    static AudioManager audioManager;
    static IKdBtService btservice;
    private static final  String ACTION ="com.driverlayer.kdos_driverServer.RemoteService";
    private static final  String PACKAGE ="com.driverlayer.kd_vwcsserver";
    private static WifiManager mWifiManager;//wifi
    private NetworkBroadcastReceiver mNetworkReceiver; // 接听网络状态发生改变的广播
    private TelephonyManager mTelephonyManager;//x信号
    private PhoneStateListener mPhoneStateListener; // 监听手机信号强度的改变
    private static  ImageView mIvBluetooth,mIvPower,mIvVedio,mIvWifi,iv_t_power;
    public  static IECarDriver ieCarDriver;//车辆aidl服务
    public static  TextView tv_speed,tv_power,tv_title_date,tv_t_power;

    @Override
    protected void onResume() {
        super.onResume();
            /*来电接受*/
        if (comingReceiver == null) {
            comingReceiver = new ComingReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("phone.iscoming");
            intentFilter.addAction("3gphone.iscoming");
            intentFilter.addAction("phone.isgone");
            registerReceiver(comingReceiver, intentFilter);
        }
        /*usb插拔接受*/
        if(usbBroadcastReceiver==null){
            usbBroadcastReceiver =new USBBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter.addDataScheme("file");
            registerReceiver(usbBroadcastReceiver, intentFilter);
        }

        if(mNetworkReceiver==null){
            IntentFilter filter = new IntentFilter();
            //WiFi相关的过滤
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //这个是真正连接上网络（数据/wifi）那一刻/断开网络的action （打开或关闭数据/wifi的那一刻并不会触发此action）
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); //Wi-Fi : enabled(打开wifi的那一刻), disabled(关闭的那一刻), enabling(正在打开), disabling(正在关闭), or unknown.
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); //正在连接,正在进行身份验证,连接成功,或连接突然中断 等状态改变的action. 当连接成功的时候会携带一些其他的数据.可以查看doc.(若保存的密码不对的话，则连接这个网络时会会一直发送这个action的Intent)
            filter.addAction(WifiManager.RSSI_CHANGED_ACTION);// 当前连接wifi强度改变的action, 当wifi连接成功的那一刻,也会发送这个action的intent

            filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED"); // wifi Ap

            filter.addAction("com.boe.action.OPEN_MIC");
            filter.addAction("com.boe.action.CLOSE_MIC");

            mNetworkReceiver = new NetworkBroadcastReceiver();
            registerReceiver(mNetworkReceiver, filter);
        }

    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_homepager;
    }

    @Override
    public void initView() {
        initDa();//fragment
        getService();//底层服务
        bindIeCarService();//aidl服务
        initNetwork();//网络
        init4G();//4g
        setWifiLevel();//状态栏wifi
        initBluetooth();//蓝牙
        init_time();//状态栏时间

        context =this;
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(mFragments.size());
        indicator.setViewPager(viewPager);

    }

    @Override
    public void findView() {
        super.findView();
        mFragments = new ArrayList<>();
        viewPager=getView(R.id.viewPager);
        indicator =getView(R.id.indicator);
        mIvBluetooth=getView(R.id.iv_blueTooth);
        mIvWifi=getView(R.id.iv_wifi);
        tv_speed=getView(R.id.tv_w_speed);
        tv_power= getView(R.id.tv_t_power);
        frameLayout =getView(R.id.frame_main);
        tv_title_date=getView(R.id.tv_title_date);
        tv_t_power=getView(R.id.tv_t_power);
        iv_t_power=getView(R.id.iv_t_power);
    }
    /*获取全局模块*/
    private void  getService(){

        if(audioManager==null) {
            audioManager = App.get().getAudioManager();
        }
        if(btservice==null) {
            btservice = App.get().getBtservice();
        }


    }
    private void bindIeCarService() {

        Intent intent = new Intent(ACTION);
        intent.setPackage(PACKAGE);

        try {
            getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }catch (Exception e){
//            LogUtils.log("绑定aidl服务失败");
        }

    }
    @Override
    public void setListener() {
        super.setListener();
        setClickListener(R.id.iv_power);
        setClickListener(R.id.title_iv_sound);
        setClickListener(R.id.center_img);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_iv_sound:
                showVolumeDialog();
                break;
            case R.id.center_img:
                hideFragment();
                break;
            case R.id.iv_power:
                JumpUtils.actAPK(this,FragmentType.CARPOWER);
                break;
        }
    }
    //初始化viewpager 数据
    private  void initDa(){
        createFragment();
        homePagerOneFragment =new HomePagerOneFragment();
        homePagerOneFragment.setFragment(this,fmFragment);
        homePagerTwoFragment =new HomePagerTwoFragment();
        homePagerTwoFragment.setHomePagerActivity(this);
        homePagerThreeFragment =new HomePagerThreeFragment();
        homePagerThreeFragment.setHomePagerActivity(this);
        mFragments.add(homePagerOneFragment);
        mFragments.add(homePagerTwoFragment);
        mFragments.add(homePagerThreeFragment);
        mCurFragment = FragmentUtils.selectFragment(this, mCurFragment, phoneFragment, R.id.frame_main);
        App.get().setmCurActivity(this);

    }

    /**
     * 初始化fragment
     */
    private void createFragment() {
      fmFragment =new FMFragment();
      fmFragment.setHomePagerActivity(this);
      btMusicFragment =new BTMusicFragment();
      musicFragment= new MusicFragment();
      phoneFragment= new PhoneFragment();
      setFragment =new SetFragment();
      appFragment=new AppFragment();
      videoFragment =new VideoFragment();
    }
    /*隐藏fragemt*/
    public static void  hideFragment(){
        frameLayout.setVisibility(View.GONE);

        freshlayout();
    }

    private static void freshlayout() {
        homePagerOneFragment.freshlayout(fmFragment);
    }
    /*显示fragment*/

    public static void showFragemnt(){
        frameLayout.setVisibility(View.VISIBLE);
    }



    /**
     * 选择Fragment
     *
     * @param fragment
     */

    private  static void switchFragment(Fragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(context, mCurFragment, fragment, R.id.frame_main);
        mCurFragment.Resume();
        myHandler.sendMessage(myHandler.obtainMessage(HandleKey.FRAME));
    }

    public static   void   switchFragmenthide(Fragment fragment) {
        mCurFragment = FragmentUtils.selectFragment(context, mCurFragment, fragment, R.id.frame_main);

    }
    public  static void  jumpFragment(@FragmentType int type ){
        switch (type){
            case  FragmentType.FM:
                switchFragment(fmFragment);
                break;
            case  FragmentType.BTMUSIC:
                switchFragment(btMusicFragment);
                break;
            case  FragmentType.MUSIC:
                switchFragment(musicFragment);
                break;
             case  FragmentType.PHONE:
                 switchFragment(phoneFragment);
                 break;
            case  FragmentType.SET:
                switchFragment(setFragment);
                break;
            case  FragmentType.APPLICATION:
                switchFragment(appFragment);
                break;
            case  FragmentType.VIDEO:
                switchFragment(videoFragment);
                break;
        }
    }
    /*viewpager适配器*/
    private class MyAdapter extends FragmentPagerAdapter {
        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.get().unregistMyReceiver();
        App.get().PauseService();

        try {
            if (comingReceiver!=null) {
                this.unregisterReceiver(comingReceiver);
            }
            if(usbBroadcastReceiver!=null){
                this.unregisterReceiver(usbBroadcastReceiver);
            }
            if(mNetworkReceiver!=null){
                this.unregisterReceiver(mNetworkReceiver);
            }
            if(serviceConnection!=null){
                getApplicationContext().unbindService(serviceConnection);
            }
            //取消信号强度监听
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
            mPhoneStateListener = null;


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (alertDialog != null) {
            dimissShow();
        }

        dialogVolumeControl=null;
        stopService(new Intent(this, PlayerService.class));

    }


    public void dimissShow() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            initImmersionBar();
        }
    }
    // 来电显示弹出框
    public void incomingShow(String number, final int index) {
        if (audioManager.requestAudioFocus(PhoneFragment.afChangeListener, 11,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            alertDialog = new Dialog(this, R.style.nodarken_style);

            alertDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.dialog_phone_incalling);
            TextView tv_info = (TextView) window.findViewById(R.id.dialog_text);
            tv_info.setText(PhoneFragment.getName(number) + "\n来电是否接听");
            Button bt_answer = (Button) window.findViewById(R.id.dialog_btn_answer);
            Button bt_refuse = (Button) window.findViewById(R.id.dialog_btn_refuse);
            bt_answer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new Thread() {
                        public void run() {
                            myHandler.sendMessage(myHandler.obtainMessage(CALL_ANSWER));
                        };
                    }.start();
//                    MainKondi.changeFragment(MainKondi.FRAGMENT_PHONE); // 接听时进入电话页面
                    jumpFragment(FragmentType.PHONE);
                    FlagProperty.flag_phone_incall_click = true;
                   dimissShow();
                }
            });
            bt_refuse.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FlagProperty.flag_phone_ringcall = false;
                    new Thread() {
                        public void run() {
                            if (index == 1) {
                                myHandler.sendMessage(myHandler.obtainMessage(CALL_HUNGUP));
                            } else if (index == 2) {
                                try {
                                    Log.d("BlueMusicBroadcoast",
                                            "btservice.btReleaseWaitingCall():" + btservice.btReleaseWaitingCall());
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                myHandler.sendMessage(myHandler.obtainMessage(CALL_HUNGUP)); //默认
                            }

                        };
                    }.start();
                  dimissShow();
                }
            });
        }
        alertDialog.show();
    }


    public static Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {

            try {
                switch (msg.what) {
                    case CALL_ANSWER:
                        btservice.btAnswerCall();
                        break;
                    case CALL_HUNGUP:
                        btservice.btHungupCall();
                        break;
                    case HandleKey.TIME:
                        tv_title_date.setText(FlagProperty.isHourdate?TimeUtils.getHour():TimeUtils.getHour_Min12());
//                        setTvText(R.id.tv_title_date,FlagProperty.isHourdate?TimeUtils.getHour():TimeUtils.getHour_Min12());
                        break;
                     case  HandleKey.POWER:
                         aidlService();
                         break;
                    case HandleKey.FRAME:
                        showFragemnt();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    };
    //日期 时间模块初始化
    private void init_time() {
        ExecutorService timePool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        timePool.execute(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        //延时一秒作用
                        Message msgtimedata = new Message();
                        msgtimedata.what = HandleKey.TIME;
                        myHandler.sendMessage(msgtimedata);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    //电量模块初始化
    private void init_power() {
        ExecutorService timePool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        timePool.execute(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        //延时5秒作用
                        Message msgtimedata = new Message();
                        msgtimedata.what = HandleKey.POWER;
                        myHandler.sendMessage(msgtimedata);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /*
     * 显示音量*/
    private  void  showVolumeDialog(){
        if(dialogVolumeControl ==null){
            dialogVolumeControl =new DialogVolumeControl();
        }

        dialogVolumeControl.setContent(this.getActivity(),this);
        dialogVolumeControl.show(getSupportFragmentManager());
    }
    /*打开高德*/
    public void  openNavi(){
        PackageManager packageManager = getPackageManager();

        String packageName = "com.autonavi.amapauto";//高德地图
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntentForPackage != null)
            startActivity(launchIntentForPackage);
        else
            Toast.makeText(this, "未安装该应用", Toast.LENGTH_SHORT).show();
    }


    // 初始化无线网络信息
    private void initNetwork() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
    }
//    蓝牙状态
    public static void initBluetooth() {
        int bluetooth_state =FlagProperty.flag_bluetooth? 1:0;
//        int bluetooth_state = Settings.System.getInt(getContentResolver(), "bluetooth_state", 1); // 默认为1, 开启
        if (bluetooth_state == 1) {
//            setIvImage(R.id.iv_blueTooth,R.mipmap.home_top_btn4_on);
            mIvBluetooth.setImageResource(R.mipmap.home_top_btn4_on);
        } else {
//            setIvImage(R.id.iv_blueTooth,R.mipmap.home_top_btn4_off);
            mIvBluetooth.setImageResource(R.mipmap.home_top_btn4_off);
        }
    }
// 行车记录仪
public static void initCarRecord() {
    int bluetooth_state =FlagProperty.flag_bluetooth? 1:0;
//        int bluetooth_state = Settings.System.getInt(getContentResolver(), "bluetooth_state", 1); // 默认为1, 开启
    if (bluetooth_state == 1) {
//            setIvImage(R.id.iv_blueTooth,R.mipmap.home_top_btn4_on);
        mIvVedio.setImageResource(R.mipmap.home_top_btn3_on);
    } else {
//            setIvImage(R.id.iv_blueTooth,R.mipmap.home_top_btn4_off);
        mIvVedio.setImageResource(R.mipmap.home_top_btn3_off);
    }
}
    /*初始化信号*/
    private void init4G() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);


                //设置信号Level
                if (!hasSimCard(getApplicationContext())) {
                    setIvImage(R.id.iv_signal,getSim(0));
                   setTvText(R.id.tv_signal,"");
                    return;
                }

                int level = NetUtils.getMobileLevel(signalStrength);
                setIvImage(R.id.iv_signal,getSim(level));
//                KLog.d("onSignalStrengthsChanged: level: " + level);
                LogUtils.log("onSignalStrengthsChanged: level: " +level);
                if (!NetUtils.isWifi(getApplicationContext())) {
                    //设置4G/2G等标识
                    //TelephonyManager 中定义了很多NETWORK_TYPE_xx的常量, 只有4G比较好区分, 2G和3G不太好区分（好像还与运营商种类有关）
                    if (mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                        setTvText(R.id.tv_signal,"4G");

                    } else {
                        setTvText(R.id.tv_signal,"E");

                    }
                } else {
                    setTvText(R.id.tv_signal,"");
                }

                //if Mobile-Data closed. (接收不到ConnectivityManager.CONNECTIVITY_ACTION 广播)
                if (!NetUtils.getMobileDataEnabled(getApplicationContext()) || level == 0) {
                    setTvText(R.id.tv_signal,"");
                }

//                if (NetUtils.getMobileDataEnabled(getApplicationContext())) {
//                    //设置4G/2G等标识
//                    //TelephonyManager 中定义了很多NETWORK_TYPE_xx的常量, 只有4G比较好区分, 2G和3G不太好区分（好像还与运营商种类有关）
//                    if (mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
//                        mTvSignal.setText("4G");
//                    } else {
//                        mTvSignal.setText("2G");
//                    }
//                } else {
//                    // TODO: 2017/6/21/021 关闭移动数据后进行的操作
//                    mTvSignal.setText("");
//                }
            }
        };
//        KLog.d("mTelephonyManager.getSimState(): " + mTelephonyManager.getSimState());

        //if (mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) { //state: SIM_STATE_UNKNOWN: 0
        if (mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
            // 获取运营商的信息
            String operator = mTelephonyManager.getSimOperator();
//            KLog.d("operator: " + operator);
            if (operator != null) {
                mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS); // 和 PhoneStateListener 中的复写的方法对应
            }
        } else {
            setIvImage(R.id.iv_signal,getSim(0));
            setTvText(R.id.tv_signal,"");
//            KLog.e("no sim");
//            Toast.makeText(this, "no sim", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean hasSimCard(Context context) {

        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
//        KLog.d("simState: " + simState);
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }

        return result;
    }
    // 设置当前所连接wifi的强度
    public static void setWifiLevel() {

        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
        if (connectionInfo != null) {
            int signalLevel = WifiManager.calculateSignalLevel(connectionInfo.getRssi(), 5);
            mIvWifi.setImageResource(getSignalIntensity(signalLevel));

        } else {
            mIvWifi.setImageResource(R.mipmap.wifi_off);

        }
    }
    // 获取不同信号强度wifi图片
    public static int getSignalIntensity(int num) {
        if (num == 5) {
            return R.mipmap.home_top_btn5_05;
        } else if (num == 4) {
            return R.mipmap.home_top_btn5_04;
        } else if (num == 3) {
            return R.mipmap.home_top_btn5_03;
        }else if (num == 2) {
            return R.mipmap.home_top_btn5_02;
        }else if (num == 1) {
            return R.mipmap.home_top_btn5_01;
        }
        else {
            return R.mipmap.home_top_btn5_01;
        }
    }
//    获取不同信号强度图片
public int getSim(int num) {
    if (num ==5) {
        return R.mipmap.home_top_btn6_04;
    } else if (num == 4) {
        return R.mipmap.home_top_btn6_04;
    }else if (num == 3) {
        return R.mipmap.home_top_btn6_03;
    } else if (num == 2) {
        return R.mipmap.home_top_btn6_02;
    }else if (num == 1) {
        return R.mipmap.home_top_btn6_01;
    }
    else {
        return R.mipmap.home_top_btn6_00;
    }
}
    //    获取不同电量图片
    public static int getPower(int num) {
        if (num == 100) {
            return R.mipmap.home_top_btn1_00;
        } else if (num>80&&num < 100) {
            return R.mipmap.home_top_btn1_06;
        }else if (num > 60&&num<=80 ){
            return R.mipmap.home_top_btn1_05;
        }else if (num > 40&&num<=60) {
            return R.mipmap.home_top_btn1_04;
        }
        else if (num>20&& num<=40) {
            return R.mipmap.home_top_btn1_03;
        }
        else if (num >5&&num<=20) {
            return R.mipmap.home_top_btn1_02;
        }
        else {
            return R.mipmap.home_top_btn1_01;
        }
    }
    @Override
    public void onBackPressed() {
       return;
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    // 接收wifi连接状态和wifi信号强度的广播
    class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            switch (action) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION: //WiFi可用状态改变(打开/关闭)
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);



//                    switch (wifiState) {
//                        case WifiManager.WIFI_STATE_DISABLED:
//                        case WifiManager.WIFI_STATE_DISABLING:
//                        case WifiManager.WIFI_STATE_UNKNOWN:
//                            mIvWifi.setImageResource(R.drawable.wifi_off);
//                            break;
//                        case WifiManager.WIFI_STATE_ENABLED:
//                        case WifiManager.WIFI_STATE_ENABLING:
//                            mIvWifi.setImageResource(R.drawable.wifi_on);
//                            break;
//                    }
                    break;
                case "android.net.wifi.WIFI_AP_STATE_CHANGED":
                    int wifi_state = intent.getIntExtra("wifi_state", WifiManager.WIFI_STATE_UNKNOWN);//EXTRA_WIFI_AP_STATE
//                    KLog.e("Liu_Net", "action: " + action + ", AP Enable状态: " + wifi_state);

                    switch (wifi_state) {
                        case 12: //WifiManager.WIFI_AP_STATE_ENABLING
                        case 13: //WifiManager.WIFI_AP_STATE_ENABLED
                            setIvImage(R.id.iv_wifi,R.mipmap.home_icon_wifi_ap);//ap
//                            mIvWifi.setImageResource(R.mipmap.home_top_btn5_01);
                            break;
                        default:
                            setIvImage(R.id.iv_wifi,R.mipmap.wifi_off);
//                            mIvWifi.setImageResource(R.drawable.wifi_off);
                            break;
                    }
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION: // wifi连接状态改变
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    if (wifiInfo != null) {
//                        mTvSignal.setVisibility(View.GONE);
//                        KLog.e("Liu_Net", "action: " + action + ", wifi 连接状态: 已连接");
                        setIvImage(R.id.iv_wifi,R.mipmap.home_top_btn5_01);
                    } else {
                        //mTvSignal.setVisibility(View.VISIBLE);
//                        KLog.e("Liu_Net", "action: " + action + ", wifi 连接状态: 已断开");
                        setIvImage(R.id.iv_wifi,R.mipmap.wifi_off);
                    }
                    break;
                case WifiManager.RSSI_CHANGED_ACTION: //wifi强度改变(已连接)
//                    KLog.e("Liu_Net", "action: " + action + ", wifi 信号强度改变");
                    setWifiLevel();
//                    setWifiAPLevel();
                    break;
                case ConnectivityManager.CONNECTIVITY_ACTION:

                    NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if(info != null && info.isConnected()) {
//                        KLog.d("发现已建立网络链接");
                        if(homePagerOneFragment!=null){
                            homePagerOneFragment.mLocationClient.stopLocation();
                            homePagerOneFragment.mLocationClient.startLocation();
                        }

                    }


//                    KLog.d("info.getState(): " + info.getState());

//                    if (!NetUtils.getMobileDataEnabled(getApplicationContext())) {
//                        mTvSignal.setText("");
//                    }
//                    if(NetworkInfo.State.CONNECTED==info.getState()){
//
//                    }else{
//                        //mIvSignal.setImageResource(mSignalResIds[0]);
//                    }
                    break;

//                case VOLUME_CHANGED_ACTION:
//                    setVolume(); //当收到音量改变的广播之后,再次设置volume
//                    break;
//                case "com.boe.action.OPEN_MIC":
//                    openMic();
//                    break;
//                case "com.boe.action.CLOSE_MIC":
//                    closeMic();
//                    break;
            }
        }
    }
//电话接受
    public class ComingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "phone.iscoming") {
                incomingShow(intent.getStringExtra("number"), intent.getIntExtra("index", 0));
            } else if (intent.getAction() == "phone.isgone") {
                dimissShow();
            } else if (intent.getAction() == "3gphone.iscoming") {
//                incoming3gShow(intent.getStringExtra("number"));
            }
        }
    }
    /*aidl服务*/
    private ServiceConnection serviceConnection =new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ieCarDriver=IECarDriver.Stub.asInterface(service);
            init_power();//状态栏电量
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ieCarDriver=null;
        }
    };
    /*车辆服务*/
    private static void  aidlService(){

        setCarWork();
        setCarMode();
        setAir();
        setCarPoewr();
    }
    /*电量车速*/
    private  static void setCarPoewr(){

        int[] power =new int[10];
        try {
            if(ieCarDriver!=null) {
                ieCarDriver.Ecoc_getGeneral_Car(power);
                if (!tv_power.getText().equals(String.valueOf(power[0]))) {
//                    setTvText(R.id.tv_t_power, String.valueOf(power[0])+"%");
                    tv_t_power.setText(String.valueOf(power[0])+"%");
                    iv_t_power.setImageResource( getPower(power[0]));
//                    setIvImage(R.id.iv_t_power, getPower(power[0]));
                }

                if (homePagerOneFragment != null) {
                    if (homePagerOneFragment.tv_w_speed.getText().toString().equals(String.valueOf(power[7]))) {
                        return;
                    }
                    if (0 == power[7]) {
                        homePagerOneFragment.tv_w_speed.setTextColor(Color.parseColor("#F03A53"));
                    } else {
                        homePagerOneFragment.tv_w_speed.setTextColor(Color.parseColor("#FFFFFF"));
                    }

                    homePagerOneFragment.tv_w_speed.setText(String.valueOf(power[7]));
                }
            }else {

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*车辆模式*/
    public static void setCarMode(){
        int[] mode =new int[2];
        try {
            if(ieCarDriver!=null){
            ieCarDriver.GetTBoxStatus(mode);
            if(FlagProperty.CarMode!=mode[1]){
                if(homePagerOneFragment!=null){
                    switch (mode[1]){
                        case 0:
                            homePagerOneFragment.tv_w_authorize.setText(R.string.默认状态);
                            break;
                        case 1:
                            homePagerOneFragment.tv_w_authorize.setText(R.string.车辆已授权);
                            break;
                        case 2:
                            homePagerOneFragment.tv_w_authorize.setText(R.string.车辆未授权);
                            break;
                    }
                }
            }
            }else {

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*车辆模式*/
    public static void setCarWork(){
        int work;
        try {
            if(ieCarDriver!=null) {
                work = ieCarDriver.getCar_WorkMode();
                if (FlagProperty.CarWork != work) {
                    if (homePagerOneFragment != null) {
                        switch (work) {
                            case 1:
                                homePagerOneFragment.tv_work.setText(R.string.Sport);
                                break;
                            case 0:
                                homePagerOneFragment.tv_work.setText("");
                                break;

                        }
                    }
                }
            }
            else {

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*空调*/
    public static void setAir(){
        int[] air=new int[8];

        try {
            if(ieCarDriver!=null) {
                staus = ieCarDriver.GetAirCon_Status(air);

                    if (homePagerOneFragment != null&&staus==0) {
                        homePagerOneFragment.setTvText(R.id.tv_air,String.valueOf(air[0])+"℃");
                    }
            }
            else {

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*空调操作成功返回0*/
    public static  int controllAir(boolean isON){
        try {
            if(ieCarDriver!=null) {
                if(staus==1){
                    return  -1;
                }
                if(isON) {
                  return   ieCarDriver.setAirCon_Para(0xffff, 0xffff, 0xffff, 0xffff, 1);//
                }
                return   ieCarDriver.setAirCon_Para(0xffff, 0xffff, 0xffff, 0xffff, 9);
            }
            else {

            }
        } catch (RemoteException e) {
            e.printStackTrace();

            return -1;
        }
        return 0;
    }

}
