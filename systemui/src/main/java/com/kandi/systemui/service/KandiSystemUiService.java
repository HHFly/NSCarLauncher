package com.kandi.systemui.service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Instrumentation;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.IFmService;
import android.os.IKdBtService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kandi.systemui.R;
import com.kandi.systemui.driver.DriverServiceManger;
import com.kandi.systemui.listen.MyPhoneStateListener;
import com.kandi.systemui.receiver.CarPowerManangerAnimRecevie;
import com.kandi.systemui.widget.DialogGunIn;
import com.kandi.systemui.widget.DialogPowerIn;
import com.kandi.systemui.widget.DialogVolumeControl;

import java.util.List;

public class KandiSystemUiService extends Service {
    Window window;
    public ComingReceiver comingReceiver;
    WindowManager mWindowManager,wm;
    LayoutParams wmParams,wmParamsBottom; // WindowManager.LayoutParams
    FrameLayout mFloatLayout ,phoneFloatLayout,mBottomLayout;
    private ImageView status_bar_wifi_btn,status_bar_3g_level_btn,status_bar_bluetooth_image,status_bar_3g_type_btn,status_bar_battery_imageView,center_img,iv_power,title_iv_sound,iv_car_tbox;
    private  TextView tv_t_power,status_bar_time_textview,tv_hangup,tv_answser,tv_phone_number,tv_home,tv_t_volume;
    private RelativeLayout Rlcenter;
    private TextView ll_volume,ll_power;
    private DialogVolumeControl dialogVolumeControl ;
    private DialogGunIn dialogGunIn;
    BluetoothController mBluetoothController;
    CarPowerManangerAnimRecevie carPowerManangerAnimRecevie;
    BatteryAndTimeController mBatteryAndTimeController;
    WifiController mWifiController;
    TelephonySignalController mTelephonySignalController;
    MyPhoneStateListener MyListener;
    private TelephonyManager Tel;
    private static final  String ACTION ="com.kangdi.systemui.SystemUIService";
    public final static String ACTION_WHEEL_VOLUMEADD = "com.kangdi.BroadCast.WheelVolumeAdd";/*音量+*/
    public final static  String ACTION_WHEEL_VOLUMEREDUCE ="com.kangdi.BroadCast.WheelVolumeReduce";//音量-
    public final static String ACTION_WHEEL_MUTE ="com.kangdi.BroadCast.Mute";//静音
    public final static String ACTION_RESET_CLEAR ="com.kandi.MASTER_CLEAR";//恢复出厂设置
    private float mPosX,mPosY,mCurPosX,mCurPosY;
  public   static IKdBtService btservice;//蓝牙服务
    private WindowManager.LayoutParams wmParamDiaglogs = null;
    private  boolean isRecord =false;
    DialogPowerIn dialogPowerIn;

    AudioManager audioManager;
    private IFmService radio;  //收音机
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DriverServiceManger.getInstance().startService(this);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        window = new Dialog(this, R.style.nodarken_style).getWindow();
        //设置Window为全透明
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        createFloatView();
        createView();
        createBottom();
        setListen();
        initHeader();
        Receiver();
        /*初始化蓝牙*/
        try {
            btservice = IKdBtService.Stub.asInterface(ServiceManager.getService("bt"));
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumelast=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC );
            // radio初始化
            radio = IFmService.Stub.asInterface(ServiceManager.getService("fm"));
        }catch (Exception e){}
        dialogVolumeControl =new DialogVolumeControl();
        dialogVolumeControl.setContent(this);
        dialogVolumeControl.incomingShow();
        setBluetoothState(SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        if(mBottomLayout!=null){
            mWindowManager.removeView(mBottomLayout);
        }
        if (phoneFloatLayout != null) {
            wm.removeView(phoneFloatLayout);
        }
            if (comingReceiver!=null){
        this.unregisterReceiver(comingReceiver);
        }
    }


    // 初始化顶部的东西
    protected void initHeader() {

        mBluetoothController = new BluetoothController(this);
        carPowerManangerAnimRecevie =new CarPowerManangerAnimRecevie(this);
        mBatteryAndTimeController = new BatteryAndTimeController(this);
        mWifiController = new WifiController(this);
        mTelephonySignalController = new TelephonySignalController(this);

        MyListener = new MyPhoneStateListener();
        MyListener.setContext(getApplicationContext());
        Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    private void createBottom(){
        wmParamsBottom=  new LayoutParams();
        wmParamsBottom.type = 2019;
        wmParamsBottom.format = PixelFormat.RGBA_8888;
        wmParamsBottom.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParamsBottom.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        wmParamsBottom.x = 0;
        wmParamsBottom.y = 0;
        wmParamsBottom.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParamsBottom.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(this);
        mBottomLayout = (FrameLayout) inflater.inflate(R.layout.titlebar_new, null);
        mWindowManager.addView(mBottomLayout, wmParamsBottom);

        //        中间栏
        center_img =(ImageView) mBottomLayout.findViewById(R.id.center_img);
        iv_power =(ImageView) mBottomLayout.findViewById(R.id.iv_power);
        title_iv_sound =(ImageView) mBottomLayout.findViewById(R.id.title_iv_sound);
        tv_home =(TextView) mBottomLayout.findViewById(R.id.tv_home);
        tv_t_volume =(TextView) mBottomLayout.findViewById(R.id.tv_t_volume);
        Rlcenter =(RelativeLayout) mBottomLayout.findViewById(R.id.center);
    }
    private void createView() {
        wmParams=  new LayoutParams();
//        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        wmParams.type = LayoutParams.TYPE_STATUS_BAR;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(this);
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.framelayout_home_titlebar, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        //wifi
        status_bar_wifi_btn = (ImageView) mFloatLayout.findViewById(R.id.iv_wifi);
        //通信信号
        status_bar_3g_type_btn = (ImageView) mFloatLayout.findViewById(R.id.tv_signal);
        status_bar_3g_level_btn = (ImageView) mFloatLayout.findViewById(R.id.iv_signal);
        //蓝牙
        status_bar_bluetooth_image = (ImageView) mFloatLayout.findViewById(R.id.iv_blueTooth);
        //电池
        status_bar_battery_imageView = (ImageView) mFloatLayout.findViewById(R.id.iv_t_power);
        tv_t_power= (TextView)mFloatLayout.findViewById(R.id.tv_t_power);
        //tbox
        iv_car_tbox =mFloatLayout.findViewById(R.id.iv_car_tbox);
//        时间
        status_bar_time_textview = (TextView) mFloatLayout.findViewById(R.id.tv_title_date);



        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));


    }

    private void setListen(){
        center_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              gotoHome();

            }
        });
        iv_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run () {
                        try {
                            Instrumentation inst= new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run () {
                        try {
                            Instrumentation inst= new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        title_iv_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVolumeDialog();
            }
        });
        tv_t_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVolumeDialog();
            }
        });
        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        Log.d("systemui","slide " +mCurPosY+"   "+mPosY+"   "+(mCurPosY - mPosY));
                        if (mCurPosY - mPosY > 0 && (Math.abs(mCurPosY - mPosY) > 30)) {
                            //向下滑動
//                            Log.d("systemui","slide down " +mCurPosY+"   "+mPosY+"   "+(mCurPosY - mPosY));
                            Rlcenter.setVisibility(View.VISIBLE);
                            Rlcenter.animate().alpha(1f).setDuration(500).setListener(null);


                        }
                        else if (mCurPosY - mPosY < 0 && (Math.abs(mPosY - mCurPosY) > 30)&&(isHome())) {
                            //向上滑动
//                            Log.d("systemui","slide up " +mCurPosY+"   "+mPosY+"   "+(mCurPosY - mPosY));
                            Rlcenter.animate()
                                    .alpha(0f)
                                    .setDuration(500)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            Rlcenter.setVisibility(View.GONE);
                                        }
                                    });
                        }
//                        if (mCurPosX - mPosX > 0
//                                && (Math.abs(mCurPosX - mPosX) > 25)) {
//                            //向左滑動
////                            tiShi(mContext,"向左");
//
//                        } else if (mCurPosX - mPosX < 0
//                                && (Math.abs(mCurPosX - mPosX) > 25)) {
//                            //向右滑动
//
////                            tiShi(mContext,"向右");
//                        }
                        break;

                }
                return false;
            }
        });
    }
    private void Receiver(){
        if (comingReceiver == null) {
            comingReceiver = new ComingReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("nscar_fresh_sdcard");
            intentFilter.addAction(ACTION_WHEEL_VOLUMEADD);
            intentFilter.addAction(ACTION_WHEEL_VOLUMEREDUCE);
            intentFilter.addAction(ACTION_WHEEL_MUTE);
            intentFilter.addAction(ACTION_RESET_CLEAR);
            registerReceiver(comingReceiver, intentFilter);
        }
    }
//返回首页
    private boolean isCalling=false;

    public void setCalling(boolean calling) {
        isCalling = calling;
    }

    void gotoHome() {
        if(isHome()) {
            Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

            mHomeIntent.addCategory(Intent.CATEGORY_HOME);
            mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(mHomeIntent);
        }
        Log.d("isCalling", "gotoHome: "+ isCalling);
        if (!isCalling) {

            Intent intent = new Intent();
            intent.setAction("com.kangdi.home.hide");
            sendBroadcast(intent);
        }

    }
    /*打开应用*/
    public  static void actActivity(Context act, String classname, String main){
        Intent intent =new Intent();
        intent.setClassName(classname,main);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            act.startActivity(intent);

        }catch (Exception e){
//            Toast.makeText(act, R.string.未安装该应用, Toast.LENGTH_SHORT).show();
        }

    }
    /*
     * 显示音量*/
    private  void  showVolumeDialog(){
        if(dialogVolumeControl ==null){
            dialogVolumeControl =new DialogVolumeControl();
            dialogVolumeControl.setContent(this);
            dialogVolumeControl.incomingShow();
        }
          dialogVolumeControl.show();

    }
    /*
     * 显示Gunin*/
    public   void  showGunInDialog(){
        if(dialogGunIn ==null){
            dialogGunIn =new DialogGunIn();
            dialogGunIn.setContent(this);
        }
        dialogGunIn.show();

    }
    public   void  dissGunInDialog(){
        if(dialogGunIn ==null){
            dialogGunIn =new DialogGunIn();
            dialogGunIn.setContent(this);
        }
        dialogGunIn.dissmiss();

    }
    /*显示powerin*/
    public  void  showPowerInDialog(){
        if(dialogPowerIn==null){
            dialogPowerIn =new DialogPowerIn(this);
        }
        dialogPowerIn.show();
    }
    public   void  dissPowerInDialog(){
        if(dialogPowerIn==null){
            dialogPowerIn =new DialogPowerIn(this);
        }
        dialogPowerIn.dissmiss();

    }
    public DialogPowerIn getDialogPowerIn(){
        if(dialogPowerIn==null){
            dialogPowerIn =new DialogPowerIn(this);
        }
        return dialogPowerIn;
    }


    public void TopRefreshNetworkEvent(int asu, int type) {
        // int asu = event.data; //getGsmSignalStrength();
        if (asu <= 2 || asu == 99) {
            // level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
            status_bar_3g_level_btn.setImageResource(R.mipmap.home_top_btn6_01); // 断网
        } else if (asu >= 12) {
            // level = SIGNAL_STRENGTH_GREAT;
            status_bar_3g_level_btn.setImageResource(R.mipmap.home_top_btn6_04);
        } else if (asu >= 8) {
            // level = SIGNAL_STRENGTH_GOOD;
            status_bar_3g_level_btn.setImageResource(R.mipmap.home_top_btn6_03);
        } else if (asu >= 5) {
            // level = SIGNAL_STRENGTH_MODERATE;
            status_bar_3g_level_btn.setImageResource(R.mipmap.home_top_btn6_02);
        } else {
            // level = SIGNAL_STRENGTH_POOR;
            status_bar_3g_level_btn.setImageResource(R.mipmap.home_top_btn6_01);
        }
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                status_bar_3g_type_btn.setImageResource(R.mipmap.home_top_gsm_e2icon);
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                status_bar_3g_type_btn.setImageResource(R.mipmap.home_top_gsm_g3icon);
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                status_bar_3g_type_btn.setImageResource(R.mipmap.home_top_gsm_g4icon);
                break;
            default:

                status_bar_3g_type_btn.setImageResource(R.mipmap.home_top_btn6_00);
        }
    }

    public void setBetteryLevel(int level) {
        if (level > 90) {
            status_bar_battery_imageView.setImageResource(R.mipmap.home_top_btn1_06);
        } else if (level > 70) {
            status_bar_battery_imageView.setImageResource(R.mipmap.home_top_btn1_05);
        } else if (level > 50) {
            status_bar_battery_imageView.setImageResource(R.mipmap.home_top_btn1_04);
        } else if (level > 30) {
            status_bar_battery_imageView.setImageResource(R.mipmap.home_top_btn1_03);
        } else if (level > 10) {
            status_bar_battery_imageView.setImageResource(R.mipmap.home_top_btn1_02);
        } else if (level >= 0) {
            status_bar_battery_imageView.setImageResource(R.mipmap.home_top_btn1_01);
        } else {
            status_bar_battery_imageView.setImageResource(R.mipmap.home_top_btn1_00); // 充电中
        }
    }
    /*电量*/
    public void setRemainMileage(int soc){
        tv_t_power.setText(soc+"%");
    }

    public void setCurrentTime(String string) {
        status_bar_time_textview.setText(string);
    }

    public void setWifiLevel(int level) {

//        Log.d("wifi", "wifi level:" + level);
        switch (level) {
            // 如果收到正确的消息就获取WifiInfo，改变图片并显示信号强度
            case 4:
                status_bar_wifi_btn.setImageResource(R.mipmap.home_top_btn5_05);
                // Toast.makeText(MainActivity.this,
                // "信号强度：" + level + " 信号最好", Toast.LENGTH_SHORT)
                // .show();
                break;
            case 3:
                status_bar_wifi_btn.setImageResource(R.mipmap.home_top_btn5_04);
                // Toast.makeText(MainActivity.this,
                // "信号强度：" + level + " 信号较好", Toast.LENGTH_SHORT)
                // .show();
                break;
            case 2:
                status_bar_wifi_btn.setImageResource(R.mipmap.home_top_btn5_03);
                // Toast.makeText(MainActivity.this,
                // "信号强度：" + level + " 信号一般", Toast.LENGTH_SHORT)
                // .show();
                break;
            case 1:
                status_bar_wifi_btn.setImageResource(R.mipmap.home_top_btn5_02);
                // Toast.makeText(MainActivity.this,
                // "信号强度：" + level + " 信号较差", Toast.LENGTH_SHORT)
                // .show();
                break;
            case 0:
                status_bar_wifi_btn.setImageResource(R.mipmap.home_top_btn5_01);
                // Toast.makeText(MainActivity.this,
                // "信号强度：" + level + " 无信号", Toast.LENGTH_SHORT)
                // .show();
                break;
            default:
                // 以防万一
                status_bar_wifi_btn.setImageResource(R.mipmap.home_top_btn5_01);
                // Toast.makeText(MainActivity.this, "无信号",
                // Toast.LENGTH_SHORT).show();
        }

    }
        public void setTbox(int mode){
            switch (mode){
                case 0 :
                case 3 :
                   iv_car_tbox.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    iv_car_tbox.setVisibility(View.VISIBLE);
                    iv_car_tbox.setImageResource(R.mipmap.ic_tbox_close);
                    break;
                case 2:
                    iv_car_tbox.setVisibility(View.VISIBLE);
                    iv_car_tbox.setImageResource(R.mipmap.ic_tbox_wirless);
                    break;
            }
        }
    public void setBluetoothState(boolean isBluetoothEnable) {
        if (isBluetoothEnable) {
            status_bar_bluetooth_image.setImageResource(R.mipmap.home_top_btn4_on);
        } else {
            status_bar_bluetooth_image.setImageResource(R.mipmap.home_top_btn4_off);
        }
    }
    public boolean restartKdService() {
        if (!DriverServiceManger.getInstance().isServiceRunning()) {
            DriverServiceManger.getInstance().startService(this);
            return false;
        } else {
            return true;
        }
    }
    //设置语言
    public void setLocal(String local){
        switch (local){
            case "ch":tv_home.setText("首页");tv_t_volume.setText("音量");tv_hangup.setText("挂断");tv_answser.setText("接听");break;
            case "en": tv_home.setText("Home");tv_t_volume.setText("Volume");tv_hangup.setText("refuse");tv_answser.setText("answer");break;
            default: tv_home.setText("首页");tv_t_volume.setText("音量");tv_hangup.setText("挂断");tv_answser.setText("接听");break;
        }
    }
  /*判断是顶部app是否是桌面*/

    private boolean isHome() {

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
//        boolean flag=false;
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains("HomePagerActivity")) { // 说明它已经启动了
//                flag = true;
                return false;
            }
        }
        return true;
    }
    /*创建浮窗*/
    private void createFloatView() {
        wm=  (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //        wmParamDiaglogs = new WindowManager.LayoutParams();
        wmParamDiaglogs =   new LayoutParams();


        //        wmParamDiaglogs.type=2002;          //type是关键，这里的2002表示系统级窗口
        wmParamDiaglogs.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParamDiaglogs.format= PixelFormat.RGBA_8888;//设置图片格式，效果为背景透明


        wmParamDiaglogs.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;//
        wmParamDiaglogs.gravity = Gravity.LEFT|Gravity.TOP;//
        wmParamDiaglogs.x = 0;
        wmParamDiaglogs.y = 0;
        wmParamDiaglogs.width=WindowManager.LayoutParams.WRAP_CONTENT;
        wmParamDiaglogs.height= WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(this);
        phoneFloatLayout = (FrameLayout) inflater.inflate(R.layout.dialog_phone, null);


        wm.addView(phoneFloatLayout, wmParamDiaglogs);

        //接听拒绝
        phoneFloatLayout.findViewById(R.id.rl_bg).getBackground().setAlpha(60);
        phoneFloatLayout.setVisibility(View.GONE);
        tv_answser =(TextView) phoneFloatLayout.findViewById(R.id.tv_answser);
        tv_hangup = (TextView) phoneFloatLayout.findViewById(R.id.tv_hangup);
        tv_phone_number =(TextView) phoneFloatLayout.findViewById(R.id.tv_phone_number);
        phoneFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv_hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btservice.btHungupCall();
                    phoneFloatLayout.setVisibility(View.GONE);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        tv_answser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btservice.btAnswerCall();
                    tv_answser.setVisibility(View.GONE);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        phoneFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmParamDiaglogs.x=(int) event.getRawX() - phoneFloatLayout.getWidth()/2;
                wmParamDiaglogs.y=(int) event.getRawY()-phoneFloatLayout.getHeight()/2-80;
                wm.updateViewLayout(phoneFloatLayout,wmParamDiaglogs);
                return false;
            }
        });


    }
    public void  showPhone(String num,int index){
        if(isHome()){
            if (audioManager.requestAudioFocus(afChangeListener, 11,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //MainKondi.changeFragment(MainKondi.FRAGMENT_PHONE); // 拨打时时进入电话页面
            }
            if(phoneFloatLayout!=null){
                phoneFloatLayout.setVisibility(View.VISIBLE);
            }
            if(tv_phone_number!=null){
                tv_phone_number.setText( num);
            }
            if(tv_hangup!=null){
                tv_hangup.setVisibility(View.VISIBLE);
            }
            if(tv_answser!=null){
                tv_answser.setVisibility(View.VISIBLE);
            }
        }else {
            Intent intent  =new Intent();
            intent.putExtra("com.kangdi.key.phonenum",num);
            intent.putExtra("com.kangdi.key.callindex",index);
            intent.setAction("com.kangdi.BroadCast.RingCall.Launcher");
            sendBroadcast(intent);
        }
    }

    public void CALLSTART(String num,int index){
        if(!isHome()){
            Intent intent  =new Intent();
            intent.putExtra("com.kangdi.key.phonenum",num);
            intent.putExtra("com.kangdi.key.callindex",index);
            intent.setAction("com.kangdi.BroadCast.CallStart.Launcher");
            sendBroadcast(intent);
        }
    }
    public void CALLEND(int index){
        if(!isHome()){
            Intent intent  =new Intent();
            intent.putExtra("com.kangdi.key.callindex",index);
            intent.setAction("com.kangdi.BroadCast.CallEnd.Launcher");
            sendBroadcast(intent);
        }
        audioManager.abandonAudioFocus(afChangeListener) ;
    }
    public void CALLOUT(String num){
        if(!isHome()){
            Intent intent  =new Intent();
            intent.putExtra("com.kangdi.key.phonenum",num);
            intent.setAction("com.kangdi.BroadCast.CallOutGoing.Launcher");
            sendBroadcast(intent);
        }else {
            if (audioManager.requestAudioFocus(afChangeListener, 11,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.d("kondi", "BtPhone get AudioFocus");
                //MainKondi.changeFragment(MainKondi.FRAGMENT_PHONE); // 拨打时时进入电话页面
            }
        }
    }
    /*音频焦点管理*/
    public static AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            }

        }
    };
    public void  hidePhone(){
        if(phoneFloatLayout!=null){
            phoneFloatLayout.setVisibility(View.GONE);
        }

    }
    /*多功能方向盘接听蓝牙电话*/
    public void  wheelAnswser(){
        tv_answser.setVisibility(View.GONE);
    }
    /*多功能方向盘挂断蓝牙电话*/
    public void  wheelHangup(){
        if(phoneFloatLayout!=null) {
            phoneFloatLayout.setVisibility(View.GONE);
        }

    }
    private  int volumelast =0;
    public void  recordVolume(){
        if(!isRecord) {
            volumelast = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            isRecord=true;
        }
    }
    public void setVolume(int volume){
        volumelast=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC );
        isRecord=true;
        if(dialogVolumeControl!=null){
            dialogVolumeControl.setVolumeProgressV(volume);
        }
    }
    public void reSetVolume(){
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumelast, 0);
        if(dialogVolumeControl!=null){
            dialogVolumeControl.setVolumeProgressV(volumelast);
        }
        isRecord=false;
    }
    public IFmService getRadio() {
        return radio;
    }
    /*来电广播接听*/

    public class ComingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "nscar_fresh_sdcard") {
                Log.d("ComingReceiver", "onReceive: ");
                Uri dirUri = Uri.parse("file://" + Environment.getExternalStorageDirectory());

                Intent it = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                it.putExtra("userStatus",true);
                sendBroadcast(it);

            }else if (intent.getAction().equals(ACTION_WHEEL_VOLUMEADD)) {
                if( SystemProperties.getInt("sys.kd.revers",0)==0) { // 1 倒车
                    showVolumeDialog();
                    int progress = dialogVolumeControl.getVolumeProgress() + 5;
                    if(progress >= 100){
                        progress = 100;
                        dialogVolumeControl.setVolumeProgress(progress);
                    }else{
                        dialogVolumeControl.setVolumeProgress(progress);
                    }
                }
            }else if (intent.getAction().equals(ACTION_WHEEL_VOLUMEREDUCE)) {
                if( SystemProperties.getInt("sys.kd.revers",0)==0) { // 1 倒车
                    showVolumeDialog();
                    int progress = dialogVolumeControl.getVolumeProgress() - 5;
                    if(progress <= 0) {
                        progress = 0;
                        dialogVolumeControl.setVolumeProgress(progress);
                    }else{
                        dialogVolumeControl.setVolumeProgress(progress);
                    }
                }
            }else if (intent.getAction().equals(ACTION_WHEEL_MUTE)) {
                if( SystemProperties.getInt("sys.kd.revers",0)==0) { // 1 倒车
                    showVolumeDialog();
                    dialogVolumeControl.setVolumeMute();
                }
            }else if (intent.getAction().equals(ACTION_RESET_CLEAR)) {
                sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
            }
        }
    }











}
