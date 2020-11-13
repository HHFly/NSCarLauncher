package com.kandi.dell.nscarlauncher.ui_portrait.carctrl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.candriver.CarInfoDriver;
import com.kandi.dell.nscarlauncher.candriver.CarSettingDriver;
import com.kandi.dell.nscarlauncher.candriver.DriverServiceManger;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.candriver.CarSettingDriver.eWindow;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class CarCtrlFragment extends BaseFragment{
    private HomePagerActivity homePagerActivity;
    CarSettingDriver carSettingDrv;
    BaseReceiver baseReceiver;
    CarInfoDriver carInfoDriver;
    int carspeed = 0;
    boolean isbreak = false;
    Thread thread;
    private final int UPDATE_PANNEL = 1;
    RelativeLayout bt_car_win_layout;

    public boolean isDoorStatus;
    public boolean isTrunkStatus;

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.CARCONTROLL);
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_carctrl;
    }

    @Override
    public void findView() {
        bt_car_win_layout = getView(R.id.bt_car_win_layout);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
        setClickListener(R.id.bt_car_near_light_layout);
        setClickListener(R.id.bt_car_far_light_layout);
        setClickListener(R.id.bt_car_alert_light_layout);
        setClickListener(R.id.bt_car_backfog_light_layout);
        setClickListener(R.id.bt_car_door_layout);
        setClickListener(R.id.bt_car_win_layout);
        setClickListener(R.id.bt_car_trunk_layout);
        setClickListener(R.id.bt_car_lightoff_layout);
//        bt_car_win_layout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.i("testtest","=====event.getAction()======"+event.getAction());
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        Log.i("testtest","=====ACTION_DOWN======");
//                        try {
//                            selectBtView(R.id.bt_car_win_layout,R.id.bt_car_win_img,R.id.bt_car_win_txt,true);
//                            //刷新界面
//                            isShowView(R.id.car_left_win_down,true);
//                            isShowView(R.id.car_right_win_down,true);
//                            //调用can接口发送报文
//                            carSettingDrv.triggerOneKeyWinOpen();
//                        }catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        Log.i("testtest","=====ACTION_UP======");
//                        selectBtView(R.id.bt_car_win_layout,R.id.bt_car_win_img,R.id.bt_car_win_txt,false);
//                        //刷新界面
//                        isShowView(R.id.car_left_win_down,false);
//                        isShowView(R.id.car_right_win_down,false);
//                        break;
//
//                }
//                return false;
//            }
//        });
    }

    @Override
    public void initView() {
        if(baseReceiver == null){
            baseReceiver = new BaseReceiver();
            IntentFilter kdIntentFilter = new IntentFilter();
            kdIntentFilter.addAction("com.driverlayer.kdos_driverserver");
            getActivity().registerReceiver(baseReceiver, kdIntentFilter);
        }
        carSettingDrv = DriverServiceManger.getInstance().getCarSettingDriver();
    }

    @Override
    public void onResume() {
        super.onResume();
        setViewVisibility(R.id.loadingView,false);
        refreshCarSettingViewButtonsStatus();
    }

    @Override
    public void Resume() {
        isbreak = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isbreak){
                    try{
                        myHandler.sendEmptyMessage(UPDATE_PANNEL);
                        Thread.sleep(1000);
                    }catch (Exception e){
                    }
                }
            }
        });
        thread.setName("CarCtrlFragment");
        thread.start();
    }

    @Override
    public void Pause() {
        isbreak = true;
    }

    @Override
    public void onDestroyView() {
        if(baseReceiver != null){
            getActivity().unregisterReceiver(baseReceiver);
            baseReceiver = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        isbreak = true;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(carSettingDrv == null){
            carSettingDrv = DriverServiceManger.getInstance().getCarSettingDriver();
        }
        try{
            switch (view.getId()){
                case R.id.bt_back:
                    homePagerActivity.hideFragment();
                    break;
                case R.id.bt_car_near_light_layout:
                    selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,true);
                    selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,false);
                    //刷新界面
                    isShowView(R.id.car_near_light,true);
                    isShowView(R.id.car_far_light,false);
                    //调用can接口发送报文
                    carSettingDrv.setMainLightState(CarSettingDriver.eMainLightState.NEAR_LIGHT);
                    break;
                case R.id.bt_car_far_light_layout:
                    selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,false);
                    selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,true);
                    //刷新界面
                    isShowView(R.id.car_near_light,false);
                    isShowView(R.id.car_far_light,true);
                    //调用can接口发送报文
                    carSettingDrv.setMainLightState(CarSettingDriver.eMainLightState.FAR_LIGHT);
                    break;
                case R.id.bt_car_alert_light_layout:
                    boolean car_alert_light_status = getView(R.id.bt_car_alert_light_layout).isSelected();
                    selectBtView(R.id.bt_car_alert_light_layout,R.id.bt_car_alert_light_img,R.id.bt_car_alert_light_txt,!car_alert_light_status);
                    //刷新界面
                    isShowView(R.id.car_alert_light,!car_alert_light_status);
                    //调用can接口发送报文
                    carSettingDrv.setFlashLightOn(!car_alert_light_status);
                    break;
                case R.id.bt_car_backfog_light_layout:
                    boolean car_backfog_light_status = getView(R.id.bt_car_backfog_light_layout).isSelected();
                    selectBtView(R.id.bt_car_backfog_light_layout,R.id.bt_car_backfog_light_img,R.id.bt_car_backfog_light_txt,!car_backfog_light_status);
                    //刷新界面
                    isShowView(R.id.car_fog_light,!car_backfog_light_status);
                    //调用can接口发送报文
                    carSettingDrv.setFogLightRearOn(!car_backfog_light_status);
                    break;
                case R.id.bt_car_door_layout:
                    //调用can接口发送报文
                    CarInfoDriver carInfoDriver = DriverServiceManger.getInstance().getCarInfoDriver();
                    int carspeed = 0;
                    if(carInfoDriver != null){
                        carInfoDriver.getCarInfo();
                        carspeed = carInfoDriver.getCarSpeed();
                    }
                    if(carspeed > 5){
                        break;
                    }
                    boolean car_door_status = getView(R.id.bt_car_door_layout).isSelected();
                    selectBtView(R.id.bt_car_door_layout,R.id.bt_car_door_img,R.id.bt_car_door_txt,!car_door_status);
                    //刷新界面
                    if(car_door_status){
                        isShowView(R.id.cardoor_unlock,true);
                        isShowView(R.id.cardoor_lock,false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowView(R.id.cardoor_unlock,false);
                            }
                        },1500);
                    } else {
                        isShowView(R.id.cardoor_lock,true);
                        isShowView(R.id.cardoor_unlock,false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowView(R.id.cardoor_lock,false);
                            }
                        },1500);
                    }
                    carSettingDrv.setDoorsLocked(!car_door_status);
                    break;
                case R.id.bt_car_win_layout:
                    selectBtView(R.id.bt_car_win_layout,R.id.bt_car_win_img,R.id.bt_car_win_txt,true);
                    //刷新界面
                    isShowView(R.id.car_left_win_down,true);
                    isShowView(R.id.car_right_win_down,true);
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //刷新界面
                            selectBtView(R.id.bt_car_win_layout,R.id.bt_car_win_img,R.id.bt_car_win_txt,false);
                            isShowView(R.id.car_left_win_down,false);
                            isShowView(R.id.car_right_win_down,false);
                        }
                    },1500);
                    //调用can接口发送报文
                    carSettingDrv.triggerOneKeyWinOpen();
                    break;
                case R.id.bt_car_trunk_layout:
                    carInfoDriver = DriverServiceManger.getInstance().getCarInfoDriver();
                    carspeed = 0;
                    if(carInfoDriver != null){
                        carspeed = carInfoDriver.getCarSpeed();
                    }
                    if(carspeed > 5){
                        break;
                    }
                    selectBtView(R.id.bt_car_trunk_layout,R.id.bt_car_trunk_img,R.id.bt_car_trunk_txt,true);
                    //刷新界面
                    isShowView(R.id.car_trunk_lock_on,true);
                    isShowView(R.id.car_trunk_lock_off,false);
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isShowView(R.id.car_trunk_lock_on,false);
                        }
                    },1500);
                    //调用can接口发送报文
                    carSettingDrv.setBackDoorOpen(false);
                    break;
                case R.id.bt_car_lightoff_layout:
                    //selectBtView(R.id.bt_car_lightoff_layout,R.id.bt_car_lightoff_img,R.id.bt_car_lightoff_txt,true);
                    selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,false);
                    selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,false);
                    selectBtView(R.id.bt_car_alert_light_layout,R.id.bt_car_alert_light_img,R.id.bt_car_alert_light_txt,false);
                    selectBtView(R.id.bt_car_backfog_light_layout,R.id.bt_car_backfog_light_img,R.id.bt_car_backfog_light_txt,false);
                    //刷新界面
                    isShowView(R.id.car_near_light,false);
                    isShowView(R.id.car_far_light,false);
                    isShowView(R.id.car_alert_light,false);
                    isShowView(R.id.car_fog_light,false);
                    //调用can接口发送报文
                    carSettingDrv.setMainLightState(CarSettingDriver.eMainLightState.OFF);
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 更改选中状态
     * @param layoutid
     * @param imgid
     * @param txtid
     * @param status
     */
    public void selectBtView(int layoutid,int imgid,int txtid, boolean status){
        getView(layoutid).setSelected(status);
        getView(imgid).setSelected(status);
        getView(txtid).setSelected(status);
    }

    /**
     * 变更状态显隐藏
     * @param viewid
     * @param isShow
     */
    public void isShowView(int viewid,boolean isShow){
        if(isShow){
            getView(viewid).setVisibility(View.VISIBLE);
        }else{
            getView(viewid).setVisibility(View.INVISIBLE);
        }
    }

    public void refreshCarSettingViewButtonsStatus() {
        CarSettingDriver carSettingDrv = DriverServiceManger.getInstance().getCarSettingDriver();

        if(carSettingDrv != null) {
            try {

                int ret = carSettingDrv.retreveCarInfo();
                Log.i("testtest","=======refreshCarSettingViewButtonsStatus======="+ret);
                if(ret == 1){
                    setViewVisibility(R.id.loadingView,true);
                    return;
                } else {
                    setViewVisibility(R.id.loadingView,false);
                }


                //车门
                selectBtView(R.id.bt_car_door_layout,R.id.bt_car_door_img,R.id.bt_car_door_txt,carSettingDrv.isDoorsLocked());
                //刷新界面
                if(isDoorStatus != carSettingDrv.isDoorsLocked()){
                    isDoorStatus = carSettingDrv.isDoorsLocked();
                    if(carSettingDrv.isDoorsLocked()){
                        isShowView(R.id.cardoor_unlock,true);
                        isShowView(R.id.cardoor_lock,false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowView(R.id.cardoor_unlock,false);
                            }
                        },1500);
                    } else {
                        isShowView(R.id.cardoor_lock,true);
                        isShowView(R.id.cardoor_unlock,false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowView(R.id.cardoor_lock,false);
                            }
                        },1500);
                    }
                }

                //后备箱
                selectBtView(R.id.bt_car_trunk_layout,R.id.bt_car_trunk_img,R.id.bt_car_trunk_txt,carSettingDrv.isBackDoorOpen());
                if(isTrunkStatus != carSettingDrv.isBackDoorOpen()){
                    isTrunkStatus = carSettingDrv.isBackDoorOpen();
                    if(carSettingDrv.isBackDoorOpen()){
                        isShowView(R.id.car_trunk_lock_on,true);
                        isShowView(R.id.car_trunk_lock_off,false);
                    }else{
                        isShowView(R.id.car_trunk_lock_on,false);
                        isShowView(R.id.car_trunk_lock_off,true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowView(R.id.car_trunk_lock_off,false);
                            }
                        },1500);
                    }
                }

                //车窗
                switch(carSettingDrv.getWindowAction(eWindow.LEFT)) {
                    case OPENING:
                        isShowView(R.id.car_left_win_down,true);
                        break;
                    case CLOSING:
                        isShowView(R.id.car_left_win_up,true);
                        break;
                    case STOPPED:
                    default:
                        isShowView(R.id.car_left_win_down,false);
                        isShowView(R.id.car_left_win_up,false);
                }

                switch(carSettingDrv.getWindowAction(eWindow.RIGHT)) {
                    case OPENING:
                        isShowView(R.id.car_right_win_down,true);
                        break;
                    case CLOSING:
                        isShowView(R.id.car_right_win_up,true);
                        break;
                    case STOPPED:
                    default:
                        isShowView(R.id.car_right_win_down,false);
                        isShowView(R.id.car_right_win_up,false);
                }

                //位置灯		0x08	1			0x01:开启/0x02:关闭
                selectBtView(R.id.bt_car_alert_light_layout,R.id.bt_car_alert_light_img,R.id.bt_car_alert_light_txt,carSettingDrv.isPositionLightOn());
                isShowView(R.id.car_alert_light,carSettingDrv.isPositionLightOn());

                //后雾灯		0x07	1			0x01:开启/0x02:关闭
                selectBtView(R.id.bt_car_backfog_light_layout,R.id.bt_car_backfog_light_img,R.id.bt_car_backfog_light_txt,carSettingDrv.isFogLightRearOn());
                isShowView(R.id.car_fog_light,carSettingDrv.isFogLightRearOn());

                //大灯		0x05	1			0x01:远光灯/0x02:近光灯/0x03:关闭
                switch(carSettingDrv.getMainLightState()) {
                    case OFF:
                        selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,false);
                        selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,false);
                        isShowView(R.id.car_near_light,false);
                        isShowView(R.id.car_far_light,false);
                        break;
                    case NEAR_LIGHT:
                        selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,true);
                        selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,false);
                        isShowView(R.id.car_near_light,true);
                        isShowView(R.id.car_far_light,false);
                        break;
                    case FAR_LIGHT:
                        selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,false);
                        selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,true);
                        isShowView(R.id.car_near_light,false);
                        isShowView(R.id.car_far_light,true);
                        break;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            //Toast.makeText(this.getActivity(), getString(R.string.back_service_not_start), Toast.LENGTH_LONG).show();
            Log.e("KDSERVICE", "CarSettingFg.onClick() service is null");
        }
    }

    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PANNEL:
                    refreshCarSettingViewButtonsStatus();
                    break;
                default:
                    break;
            }
        };
    };

    private class BaseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Set<String> keySet = bundle.keySet();
            for(String key : keySet) {
                if(key.compareTo("KD_CAST_EVENT3") != 0) {
                    continue;
                }
                myHandler.sendEmptyMessage(UPDATE_PANNEL);
            }
        }
    }

}
