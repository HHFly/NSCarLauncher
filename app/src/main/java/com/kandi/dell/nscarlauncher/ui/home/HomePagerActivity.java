package com.kandi.dell.nscarlauncher.ui.home;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
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
import android.os.SystemProperties;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.driverlayer.kdos_driverServer.IECarDriver;
import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.Activity.BaseActivity;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.common.util.IsHomeUtils;
import com.kandi.dell.nscarlauncher.common.util.JumpUtils;
import com.kandi.dell.nscarlauncher.common.util.LanguageUtil;
import com.kandi.dell.nscarlauncher.common.util.LogUtils;
import com.kandi.dell.nscarlauncher.common.util.NetUtils;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.common.util.TimeUtils;
import com.kandi.dell.nscarlauncher.common.util.ToastUtils;
import com.kandi.dell.nscarlauncher.receiver.CarMFLReceiver;
import com.kandi.dell.nscarlauncher.ui.application.AppFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.BTMusicFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.fm.FMFragment;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerThreeFragment;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
import com.kandi.dell.nscarlauncher.ui.home.model.HomeModel;
import com.kandi.dell.nscarlauncher.ui.home.receiver.USBBroadcastReceiver;
import com.kandi.dell.nscarlauncher.ui.home.receiver.USBReceover;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.phone.PhoneFragment;
import com.kandi.dell.nscarlauncher.ui.setting.SetFragment;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.BlueToothSetFragment;
import com.kandi.dell.nscarlauncher.ui.video.VideoFragment;
import com.kandi.dell.nscarlauncher.widget.DialogVolumeControl;
import com.kandi.dell.nscarlauncher.widget.ViewPagerScroller;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.relex.circleindicator.CircleIndicator;

import static com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty.staus;


public class HomePagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    public static final int CALL_ANSWER = 4; // 接听来电
    public static final int CALL_HUNGUP = 5; // 挂断来电
    private static FrameLayout frameLayout;//主界面
   static HomePagerOneFragment homePagerOneFragment;

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
    public static BlueToothSetFragment blueToothSetFragment;//蓝牙设置
    public  static  BaseActivity context;
    private ArrayList<HomeModel> mData;
    static Dialog alertDialog;//来电弹框
    public ComingReceiver comingReceiver;
    public USBBroadcastReceiver usbBroadcastReceiver;
    public CarMFLReceiver  carMFLReceiver;
    public USBReceover usbReceover;
    static AudioManager audioManager;
    static IKdBtService btservice;
    private static final  String ACTION ="com.driverlayer.kdos_driverServer.RemoteService";
    private static final  String PACKAGE ="com.driverlayer.kd_vwcsserver";
    private static final  String ACTIONHOME ="com.kangdi.home.hide";
    private static WifiManager mWifiManager;//wifi
    private NetworkBroadcastReceiver mNetworkReceiver; // 接听网络状态发生改变的广播
    private TelephonyManager mTelephonyManager;//x信号
    private PhoneStateListener mPhoneStateListener; // 监听手机信号强度的改变
    private static  ImageView mIvBluetooth,mIvPower,mIvVedio,mIvWifi,iv_t_power;
    public  static IECarDriver ieCarDriver;//车辆aidl服务
    public static  TextView tv_speed,tv_power,tv_title_date,tv_t_power;
    static  TranslateAnimation mHiddenAction,mShowAction;
    public  static HomePagerActivity homePagerActivity;

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
            intentFilter.addAction("com.changeBg");
            registerReceiver(comingReceiver, intentFilter);
        }
        /*usb插拔接受*/
        if(usbReceover==null){
            usbReceover =new USBReceover();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
            intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
            registerReceiver(usbReceover, intentFilter);
        }
        if(usbBroadcastReceiver==null){
            usbBroadcastReceiver =new USBBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED"); //应用安装
            intentFilter.addAction("com.kangdi.home.hide");
            intentFilter.addDataScheme("file");
            registerReceiver(usbBroadcastReceiver, intentFilter);
        }

        /*车辆方向盘*/
        if(carMFLReceiver ==null){
            carMFLReceiver =new CarMFLReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_MODE);
            intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_VOICE);
            intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_MUSIC_PREV);
            intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_MUSIC_NEXT);
            intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_CALL);
            intentFilter.addAction(CarMFLReceiver.ACTION_WHEEL_HANGUP);
            registerReceiver(carMFLReceiver, intentFilter);
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
            filter.addAction(ACTIONHOME);
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
        Log.d("homepager","onCreate");
        initDa();//fragment
        getService();//底层服务
        bindIeCarService();//aidl服务
        initNetwork();//网络
//        init4G();//4g
//        setWifiLevel();//状态栏wifi
        initBluetooth();//蓝牙
//        init_time();//状态栏时间
        initAnim();
        context =this;
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(mFragments.size());
        ViewPagerScroller pagerScroller = new ViewPagerScroller(getActivity());
        pagerScroller.setScrollDuration(1000);//设置时间，时间越长，速度越慢
        pagerScroller.initViewPagerScroll(viewPager);
        indicator.setViewPager(viewPager);
        //获取usb权限
//        openUsbDevice();
        DialogLocalMusic.ScanAllDaTa(this);
    }

    private void initAnim() {
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        mShowAction.setDuration(500);

        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAction.setDuration(500);
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
        //android系统提供内置的Equalizer支持，我们可以直接声明并且使用。但必须注意，当我们在代码中使用Equalizer的时候，其实就是调整音量(EQ均衡器是改变音频使得声音发生变化，像是洪亮或者低沉)
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

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
        this.homePagerActivity=this;
        homePagerOneFragment = new HomePagerOneFragment();
        homePagerOneFragment.setFragment(this,fmFragment);
        homePagerTwoFragment = new HomePagerTwoFragment();
        homePagerTwoFragment.setHomePagerActivity(this);
        homePagerThreeFragment = new HomePagerThreeFragment();
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


            fmFragment = new FMFragment();


            fmFragment.setHomePagerActivity(this);


         btMusicFragment = new BTMusicFragment();


         musicFragment= new MusicFragment();



        phoneFragment = new PhoneFragment();


        setFragment = new SetFragment();


        appFragment = new AppFragment();


        videoFragment =new VideoFragment();

        blueToothSetFragment = new BlueToothSetFragment();


    }
    /*隐藏fragemt*/
    public static void  hideFragment(){
//        frameLayout.animate()
//                .alpha(0f)
//                .setDuration(500)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        frameLayout.setVisibility(View.GONE);
//                    }
//                });
//        frameLayout.startAnimation(mHiddenAction);
        frameLayout.setVisibility(View.GONE);
        if(homePagerActivity!=null){
            homePagerActivity.hideLoadingDialog();
        }if(FragmentType.BTSET==mCurFragment.getmType()){
            blueToothSetFragment.hideDialog();
        }
        freshlayout();
    }

    private static void freshlayout() {
        homePagerOneFragment.freshlayout(fmFragment);
    }
    /*显示fragment*/

    public static void showFragemnt(){
//        frameLayout.setAlpha(0f);
        frameLayout.setVisibility(View.VISIBLE);
//        frameLayout.animate().alpha(1f).setDuration(500).setListener(null);
//        frameLayout.startAnimation(mShowAction);

    }



    /**
     * 选择Fragment
     *
     * @param fragment
     */

    private  static void switchFragment(BaseFragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(context, mCurFragment, fragment, R.id.frame_main);
        mCurFragment.setmType(fragment.getmType());
        mCurFragment.Resume();
        myHandler.sendMessage(myHandler.obtainMessage(HandleKey.FRAME));
    }

    public static   void   switchFragmenthide(Fragment fragment) {
        mCurFragment = FragmentUtils.selectFragment(context, mCurFragment, fragment, R.id.frame_main);

    }
    public  static void  jumpFragment(@FragmentType int type ){
        if(IsHomeUtils.isForeground(context,"HomePagerActivity"))
        switch (type){
            case  FragmentType.FM:
                switchFragment(fmFragment);
                break;
            case  FragmentType.BTMUSIC:
                blueToothSetFragment.setOriginId(1);
                switchFragment(btMusicFragment);
                break;
            case  FragmentType.MUSIC:
                switchFragment(musicFragment);
                break;
             case  FragmentType.PHONE:
                 blueToothSetFragment.setOriginId(2);
                 switchFragment(phoneFragment);
                 break;
            case  FragmentType.SET:
                blueToothSetFragment.setOriginId(0);
                switchFragment(setFragment);
                break;
            case  FragmentType.APPLICATION:
                switchFragment(appFragment);
                break;
            case  FragmentType.VIDEO:
                switchFragment(videoFragment);
                break;
            case FragmentType.BTSET:
                switchFragment(blueToothSetFragment);
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

        App.get().unregistMyReceiver();
        App.get().PauseService();

        try {
            if (comingReceiver!=null) {
                this.unregisterReceiver(comingReceiver);
            }
            if(usbBroadcastReceiver!=null){
                this.unregisterReceiver(usbBroadcastReceiver);
            }
            if(usbReceover!=null){
                this.unregisterReceiver(usbReceover);
            }
            if(carMFLReceiver!=null){
                this.unregisterReceiver(carMFLReceiver);
            }
            if(mNetworkReceiver!=null){
                this.unregisterReceiver(mNetworkReceiver);
            }
            if(serviceConnection!=null){
                getApplicationContext().unbindService(serviceConnection);
            }
            //取消信号强度监听
//
//            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
//            mPhoneStateListener = null;


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (alertDialog != null) {
            dimissShow();
        }

        dialogVolumeControl=null;
        stopService(new Intent(this, PlayerService.class));
        super.onDestroy();
    }


    public static void dimissShow() {
        if (alertDialog != null) {
            alertDialog.dismiss();

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
            tv_info.setText(PhoneFragment.getName(number));
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
                   initImmersionBar();
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
                  initImmersionBar();
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
    //车辆服务模块初始化
    private void init_carservice() {
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
        if (launchIntentForPackage != null) {
            startActivity(launchIntentForPackage);
        }
        else {
            Toast.makeText(this, R.string.未安装该应用, Toast.LENGTH_SHORT).show();
        }
    }


    // 初始化无线网络信息
    private void initNetwork() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
    }
//    蓝牙状态
    public static void initBluetooth() {

        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {

            FlagProperty.flag_bluetooth = true;
        }
        int bluetooth_state =FlagProperty.flag_bluetooth? 1:0;

//        int bluetooth_state = Settings.System.getInt(getContentResolver(), "bluetooth_state", 1); // 默认为1, 开启
//        if (bluetooth_state == 1) {
////            setIvImage(R.id.iv_blueTooth,R.mipmap.home_top_btn4_on);
//            mIvBluetooth.setImageResource(R.mipmap.home_top_btn4_on);
//        } else {
////            setIvImage(R.id.iv_blueTooth,R.mipmap.home_top_btn4_off);
//            mIvBluetooth.setImageResource(R.mipmap.home_top_btn4_off);
//        }
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
        if (num == 1000) {
            return R.mipmap.home_top_btn1_00;
        } else if (num>800&&num < 1000) {
            return R.mipmap.home_top_btn1_06;
        }else if (num > 600&&num<=800 ){
            return R.mipmap.home_top_btn1_05;
        }else if (num > 400&&num<=600) {
            return R.mipmap.home_top_btn1_04;
        }
        else if (num>200&& num<=400) {
            return R.mipmap.home_top_btn1_03;
        }
        else if (num >50&&num<=200) {
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
                            if( homePagerOneFragment.mLocationClient!=null) {
                                homePagerOneFragment.mLocationClient.stopLocation();
                                homePagerOneFragment.mLocationClient.startLocation();
                            }
                        }

                    }


                    break;
                case ACTIONHOME:
                    HomePagerActivity.hideFragment();
                    break;
            }
        }
    }
//电话接受
    public class ComingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "phone.iscoming") {
                int  index =intent.getIntExtra("index", 0);
                if(2!=index) {
//                    incomingShow(intent.getStringExtra("number"), intent.getIntExtra("index", 0));
                    if (audioManager.requestAudioFocus(PhoneFragment.afChangeListener, 11,
                            AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        jumpFragment(FragmentType.PHONE);

                        PhoneFragment.callIn(intent.getStringExtra("number"),intent.getStringExtra("address"),intent.getStringExtra("type"));
                    }

                }
            } else if (intent.getAction() == "phone.isgone") {
//                dimissShow();
            } else if (intent.getAction() == "3gphone.iscoming") {
//                incoming3gShow(intent.getStringExtra("number"));
            } else if(intent.getAction() == "com.changeBg"){
                int picindex = SPUtil.getInstance(getActivity(),"picindex").getInt("picindex",0);//存放图片数组存入对应资源
                Log.i("testtes","com.changeBg-->"+picindex);
                homePagerOneFragment.changBgView(picindex);
                homePagerTwoFragment.changBgView(picindex);
                homePagerThreeFragment.changBgView(picindex);
            }
        }
    }
    /*aidl服务*/
    private ServiceConnection serviceConnection =new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ieCarDriver=IECarDriver.Stub.asInterface(service);
            init_carservice();//iecar 服务

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
        getCarState();
    }
    /*电量车程*/
    private  static void setCarPoewr(){

        int[] power =new int[12];
        try {
            if(ieCarDriver!=null) {
               ieCarDriver.getPowerManager(power);
                if (!tv_power.getText().equals(String.valueOf(power[10]))) {
                    int i =power[10]%10;
                    FlagProperty.CarPower =power[10]/10;
//                    setTvText(R.id.tv_t_power, String.valueOf(power[0])+"%");
                    tv_t_power.setText(FlagProperty.CarPower +"%");
                    iv_t_power.setImageResource(getPower(power[10]));
//                    setIvImage(R.id.iv_t_power, getPower(power[0]));
                }
                    FlagProperty.Speed =power[7];
                    /*剩余里程*/
                if (homePagerOneFragment != null) {
                    if (homePagerOneFragment.tv_w_speed.getText().toString().equals(String.valueOf(power[11]))) {
                        return;
                    }
                    if (0 == power[11]) {
                        homePagerOneFragment.tv_w_speed.setTextColor(Color.parseColor("#F03A53"));
                    } else {
                        homePagerOneFragment.tv_w_speed.setTextColor(Color.parseColor("#FFFFFF"));
                    }

                    homePagerOneFragment.tv_w_speed.setText(String.valueOf(power[11]));
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

                    if (homePagerOneFragment != null) {
                        switch (work) {
                            case 0x01:
                                homePagerOneFragment.tv_work.setText(R.string.Sport);
                                break;
                            case 0x00:
                                homePagerOneFragment.tv_work.setText(R.string.Economic);
                                break;
                            case 0x03:
                                homePagerOneFragment.tv_work.setText(R.string.Irascible);
                                break;
                            case 0x04:
                                homePagerOneFragment.tv_work.setText(R.string.NEDC);
                                break;
                                default:
                                    homePagerOneFragment.tv_work.setText(R.string.Economic);
                                    break;
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
                if(0x10==(air[4]&0x30)){
                        if(1!=HomePagerOneFragment.isAirOpen){
                            HomePagerOneFragment.isAirOpen=1;
                            App.pagerOneHnadler.sendEmptyMessage(HandleKey.AIROPEN);
                        }

                }else {
                    if(0!=HomePagerOneFragment.isAirOpen){
                        HomePagerOneFragment.isAirOpen=0;
                        App.pagerOneHnadler.sendEmptyMessage(HandleKey.AIRCLOSE);
                    }
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
    /*整车控制状态*/
    public static  void  getCarState(){
        int [] carState =new int[16];
        try {
        if(ieCarDriver!=null) {
            FlagProperty.BCMStaus= ieCarDriver.getCarState(carState);
            if(HomePagerTwoFragment.backbox!=carState[8]){
                HomePagerTwoFragment.backbox=carState[8];
                HomePagerTwoFragment.myHandler.sendEmptyMessage(3);
            }
            if (HomePagerTwoFragment.centerlock!=carState[7]){
                HomePagerTwoFragment.centerlock=carState[7];
                HomePagerTwoFragment.myHandler.sendEmptyMessage(4);
            }
        }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    /*设置后备箱状态*/
    public static  void setBackBox(boolean isOpen){
        try {
            if(FlagProperty.Speed<5) {

                ieCarDriver.setCar_Action(0x03, 1, isOpen ? 0x01 : 0x02);
            }else {
             ToastUtils.show(R.string.safetip);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*设置中控门锁*/
    public static void  setDoorLock(boolean isOpen){
        try {
            if(FlagProperty.Speed<5) {

                ieCarDriver.setCar_Action(0x01, 1, isOpen ? 0x01 : 0x02);
            }else {
                ToastUtils.show(R.string.safetip);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*一件下窗*/
    public static void  OneKeyWindowOpen(){
        try {
          ieCarDriver.set_OneKeyOpenWindow(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 内存不够时
     * @param level
     */
    @Override

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_MODERATE) {
//            TRIM_MEMORY_COMPLETE：内存不足，并且该进程在后台进程列表最后一个，马上就要被清理
//            TRIM_MEMORY_MODERATE：内存不足，并且该进程在后台进程列表的中部。
//            TRIM_MEMORY_BACKGROUND：内存不足，并且该进程是后台进程。
//            TRIM_MEMORY_UI_HIDDEN：内存不足，并且该进程的UI已经不可见了
            LanguageUtil.restartApp(this,HomePagerActivity.class);
        }
    }
    /**
     * 获得 usb 权限
     */
    private void openUsbDevice(){
        //before open usb device
        //should try to get usb permission
        tryGetUsbPermission();
    }
    UsbManager mUsbManager;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private void tryGetUsbPermission(){
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbPermissionActionReceiver, filter);

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //here do emulation to ask all connected usb device for permission
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            //add some conditional check if necessary
            //if(isWeCaredUsbDevice(usbDevice)){
            if(mUsbManager.hasPermission(usbDevice)){
                //if has already got permission, just goto connect it
                //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                //and also choose option: not ask again
                afterGetUsbPermission(usbDevice);
            }else{
                //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
            }
            //}
        }
    }


    private void afterGetUsbPermission(UsbDevice usbDevice){
        //call method to set up device communication
        //Toast.makeText(this, String.valueOf("Got permission for usb device: " + usbDevice), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()), Toast.LENGTH_LONG).show();

        doYourOpenUsbDevice(usbDevice);
    }

    private void doYourOpenUsbDevice(UsbDevice usbDevice){
        //now follow line will NOT show: User has not given permission to device UsbDevice
        UsbDeviceConnection connection = mUsbManager.openDevice(usbDevice);
        //add your operation code here

    }

    private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if(null != usbDevice){
                            afterGetUsbPermission(usbDevice);
                        }
                    }
                    else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

}
