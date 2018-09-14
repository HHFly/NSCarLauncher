package com.kandi.systemui.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kandi.systemui.R;
import com.kandi.systemui.driver.DriverServiceManger;
import com.kandi.systemui.listen.MyPhoneStateListener;
import com.kandi.systemui.widget.DialogVolumeControl;

public class KandiSystemUiService extends Service {
    WindowManager mWindowManager;
    LayoutParams wmParams; // WindowManager.LayoutParams
    FrameLayout mFloatLayout;
    private ImageView status_bar_wifi_btn,status_bar_3g_level_btn,status_bar_bluetooth_image,status_bar_3g_type_btn,status_bar_battery_imageView,center_img,iv_power,title_iv_sound;
    private  TextView tv_t_power,status_bar_time_textview;
    private DialogVolumeControl dialogVolumeControl ;
    BluetoothController mBluetoothController;
    BatteryAndTimeController mBatteryAndTimeController;
    WifiController mWifiController;
    TelephonySignalController mTelephonySignalController;
    MyPhoneStateListener MyListener;
    private TelephonyManager Tel;
    private static final  String ACTION ="com.kangdi.systemui.SystemUIService";
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

        createView();
        setListen();
        initHeader();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }

    }


    // 初始化顶部的东西
    protected void initHeader() {

        mBluetoothController = new BluetoothController(this);
        mBatteryAndTimeController = new BatteryAndTimeController(this);
        mWifiController = new WifiController(this);
        mTelephonySignalController = new TelephonySignalController(this);

        MyListener = new MyPhoneStateListener();
        MyListener.setContext(getApplicationContext());
        Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
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
//        时间
        status_bar_time_textview = (TextView) mFloatLayout.findViewById(R.id.tv_title_date);
//        中间栏
        center_img =(ImageView) mFloatLayout.findViewById(R.id.center_img);
        iv_power =(ImageView) mFloatLayout.findViewById(R.id.iv_power);
        title_iv_sound =(ImageView) mFloatLayout.findViewById(R.id.title_iv_sound);
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

                actActivity(getApplicationContext(),"com.kandi.powermanager","com.kandi.powermanager.view.PowerManagerActivity");
            }
        });
        title_iv_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVolumeDialog();
            }
        });

    }
//返回首页
    void gotoHome() {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(mHomeIntent);


    }
    /*打开应用*/
    public  static void actActivity(Context act, String classname, String main){
        Intent intent =new Intent();
        intent.setClassName(classname,main);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            act.startActivity(intent);

        }catch (Exception e){
            Toast.makeText(act, "未安装该应用", Toast.LENGTH_SHORT).show();
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

        Log.d("huachao", "wifi level:" + level);
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
}
