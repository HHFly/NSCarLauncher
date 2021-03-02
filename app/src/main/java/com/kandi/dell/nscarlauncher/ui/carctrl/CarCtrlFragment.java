package com.kandi.dell.nscarlauncher.ui.carctrl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.candriver.CarInfoDriver;
import com.kandi.dell.nscarlauncher.candriver.DriverServiceManger;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;

import java.util.Arrays;
import java.util.Set;

public class CarCtrlFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    boolean isUserClick = false;//用户操作延迟刷新
    private final int UPDATE_PANNEL = 1;
    Thread thread;
    boolean isbreak = false;
    BaseReceiver baseReceiver;
    int[] status = new int[5];

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }


    @Override
    public int getContentResId() {
        return R.layout.fragment_carctrl;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_left_light_layout);
        setClickListener(R.id.bt_right_light_layout);
        setClickListener(R.id.bt_total_light_layout);
        setClickListener(R.id.bt_near_light_layout);
        setClickListener(R.id.bt_far_light_layout);
        setClickListener(R.id.bt_overtake_light_layout);
        setClickListener(R.id.bt_rearfog_light_layout);
        setClickListener(R.id.bt_main_light1_layout);
        setClickListener(R.id.bt_main_light2_layout);
        setClickListener(R.id.bt_wiperint_gear_layout);
        setClickListener(R.id.bt_wiperlo_gear_layout);
        setClickListener(R.id.bt_wiperhi_gear_layout);
        setClickListener(R.id.bt_spray_layout);
        setClickListener(R.id.bt_autolight_layout);
        setClickListener(R.id.bt_auto_wiper_layout);
    }

    @Override
    public void initView() {
        setmType(FragmentType.CARCONTROLL);
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
    public void onResume() {
        super.onResume();
        this.refreshPannelStatus();
    }

    @Override
    public void Resume() {
        isUserClick = false;
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
    public void onDestroy() {
        isbreak = true;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(isFastDoubleClick()){
            return;
        }
        CarInfoDriver carInfoDriver = DriverServiceManger.getInstance().getCarInfoDriver();
        try {
            carInfoDriver.getCar_Status();
            for(int i=0;i<status.length;i++){
                status[i] = carInfoDriver.param1[i];
            }
            if(carInfoDriver != null){
                switch (view.getId()){
                    case R.id.bt_left_light_layout:
                        getView(R.id.bt_right_light_layout).setSelected(false);
                        if(view.isSelected()){
                            status[0] = status[0] & 0x7f;
                        }else{
                            status[0] = (status[0] & 0x3f) | (1<<7);
                        }
                        break;
                    case R.id.bt_right_light_layout:
                        getView(R.id.bt_left_light_layout).setSelected(false);
                        if(view.isSelected()){
                            status[0] = status[0] & 0xBf;
                        }else{
                            status[0] = (status[0] & 0x3f) | (1<<6);
                        }
                        break;
                    case R.id.bt_total_light_layout:
                        if(view.isSelected()){
                            getView(R.id.bt_near_light_layout).setSelected(false);
                            getView(R.id.bt_far_light_layout).setSelected(false);
                            getView(R.id.bt_rearfog_light_layout).setSelected(false);
                            status[0] = status[0] & 0xDf;
                        }else{
                            status[0] = (status[0] & 0xDf) | (1<<5);
                        }
                        break;
                    case R.id.bt_near_light_layout:
                        if(!getView(R.id.bt_total_light_layout).isSelected()){
                            return;
                        }
                        getView(R.id.bt_far_light_layout).setSelected(false);
                        if(view.isSelected()){
                            status[0] = status[0] & 0xEF;
                        }else{
                            status[0] = (status[0] & 0xE7) | (1<<4);
                        }
                        break;
                    case R.id.bt_far_light_layout:
                        if(!getView(R.id.bt_total_light_layout).isSelected()){
                            return;
                        }
                        getView(R.id.bt_near_light_layout).setSelected(false);
                        if(view.isSelected()){
                            status[0] = status[0] & 0xEF;
                        }else{
                            status[0] = (status[0] & 0xE7) | (1<<3);
                        }
                        break;
                    case R.id.bt_overtake_light_layout:
                        if(view.isSelected()){
                            status[0] = status[0] & 0xFB;
                        }else{
                            status[0] = (status[0] & 0xFB) | (1<<2);
                        }
                        break;
                    case R.id.bt_rearfog_light_layout:
                        if(!getView(R.id.bt_total_light_layout).isSelected()){
                            return;
                        }
                        if(view.isSelected()){
                            status[2] = 0;
                        }else{
                            status[2] = 1;
                        }
                        break;
                    case R.id.bt_main_light1_layout:
                        if(view.isSelected()){
                            status[3] = 0;
                        }else{
                            status[3] = 1;
                        }
                        break;
                    case R.id.bt_main_light2_layout:
                        if(view.isSelected()){
                            status[4] = 0;
                        }else{
                            status[4] = 1;
                        }
                        break;
                    case R.id.bt_wiperint_gear_layout://如果自动雨刮不是互斥需要调整
                        getView(R.id.bt_wiperlo_gear_layout).setSelected(false);
                        getView(R.id.bt_wiperhi_gear_layout).setSelected(false);
                        getView(R.id.bt_auto_wiper_layout).setSelected(false);
                        if(view.isSelected()){
                            status[1] = status[1] & 0x7F;
                        }else{
                            status[1] = (status[1] & 0x1E) | (1<<7);
                        }
                        break;
                    case R.id.bt_wiperlo_gear_layout:
                        getView(R.id.bt_wiperint_gear_layout).setSelected(false);
                        getView(R.id.bt_wiperhi_gear_layout).setSelected(false);
                        getView(R.id.bt_auto_wiper_layout).setSelected(false);
                        if(view.isSelected()){
                            status[1] = status[1] & 0xBF;
                        }else{
                            status[1] = (status[1] & 0x1E) | (1<<6);
                        }
                        break;
                    case R.id.bt_wiperhi_gear_layout:
                        getView(R.id.bt_wiperint_gear_layout).setSelected(false);
                        getView(R.id.bt_wiperlo_gear_layout).setSelected(false);
                        getView(R.id.bt_auto_wiper_layout).setSelected(false);
                        if(view.isSelected()){
                            status[1] = status[1] & 0xDF;
                        }else{
                            status[1] = (status[1] & 0x1E) | (1<<5);
                        }
                        break;
                    case R.id.bt_spray_layout:
                        if(view.isSelected()){
                            status[1] = status[1] & 0xEF;
                        }else{
                            status[1] = (status[1] & 0xEF) | (1<<4);
                        }
                        break;
                    case R.id.bt_autolight_layout:
                        if(view.isSelected()){
                            status[1] = status[1] & 0xFD;
                        }else{
                            status[1] = (status[1] & 0xFD) | (1<<1);
                        }
                        break;
                    case R.id.bt_auto_wiper_layout:
                        getView(R.id.bt_wiperint_gear_layout).setSelected(false);
                        getView(R.id.bt_wiperlo_gear_layout).setSelected(false);
                        getView(R.id.bt_wiperhi_gear_layout).setSelected(false);
                        if(view.isSelected()){
                            status[1] = status[1] & 0xFE;
                        }else{
                            status[1] = (status[1] & 0x1E) | 1;
                        }
                        break;
                }
                view.setSelected(!view.isSelected());
                carInfoDriver.setCar_Status(status);
                Log.i("testtest",""+ Arrays.toString(status));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long lastClickTime;
    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    protected final int UPDATE_TEXT = 0;

    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    refreshACDisplay();
                    break;
                case UPDATE_PANNEL:
                    if(isUserClick){
                        isUserClick = false;
                        return;
                    }
                    refreshPannelStatus();
                    break;
            }
        };
    };

    public void refreshACDisplay() {

    }

    public void refreshPannelStatus() {
        CarInfoDriver carInfoDriver = DriverServiceManger.getInstance().getCarInfoDriver();
        if(carInfoDriver != null){
            try {
                int ret = carInfoDriver.getCar_Status();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(carInfoDriver.getLeftRightLight() == 0){
                getView(R.id.bt_left_light_layout).setSelected(false);
                getView(R.id.bt_right_light_layout).setSelected(false);
            }else if(carInfoDriver.getLeftRightLight() == 1){
                getView(R.id.bt_left_light_layout).setSelected(true);
                getView(R.id.bt_right_light_layout).setSelected(false);
            }else if(carInfoDriver.getLeftRightLight() == 2){
                getView(R.id.bt_left_light_layout).setSelected(false);
                getView(R.id.bt_right_light_layout).setSelected(true);
            }

            if(carInfoDriver.getTotalLight() == 0){
                getView(R.id.bt_total_light_layout).setSelected(false);
            }else {
                getView(R.id.bt_total_light_layout).setSelected(true);
            }

            if(carInfoDriver.getNearFarLight() == 0){
                getView(R.id.bt_near_light_layout).setSelected(false);
                getView(R.id.bt_far_light_layout).setSelected(false);
            }else if(carInfoDriver.getNearFarLight() == 1){
                getView(R.id.bt_near_light_layout).setSelected(true);
                getView(R.id.bt_far_light_layout).setSelected(false);
            }else if(carInfoDriver.getNearFarLight() == 2){
                getView(R.id.bt_near_light_layout).setSelected(true);
                getView(R.id.bt_far_light_layout).setSelected(false);
            }

            if(carInfoDriver.getOvertakeLight() == 0){
                getView(R.id.bt_overtake_light_layout).setSelected(false);
            }else{
                getView(R.id.bt_overtake_light_layout).setSelected(true);
            }

            if(carInfoDriver.getRearfogLight() == 0){
                getView(R.id.bt_rearfog_light_layout).setSelected(false);
            }else{
                getView(R.id.bt_rearfog_light_layout).setSelected(true);
            }
            Log.i("testtest","=========="+carInfoDriver.getMainLight1()+"   "+carInfoDriver.getMainLight2());
            Log.i("testtest","==========="+Arrays.toString(carInfoDriver.param1));
            if(carInfoDriver.getMainLight1() == 0){
                getView(R.id.bt_main_light1_layout).setSelected(false);
            }else{
                getView(R.id.bt_main_light1_layout).setSelected(true);
            }

            if(carInfoDriver.getMainLight2() == 0){
                getView(R.id.bt_main_light2_layout).setSelected(false);
            }else{
                getView(R.id.bt_main_light2_layout).setSelected(true);
            }

            if(carInfoDriver.getWiper() == 0){
                getView(R.id.bt_wiperint_gear_layout).setSelected(false);
                getView(R.id.bt_wiperlo_gear_layout).setSelected(false);
                getView(R.id.bt_wiperhi_gear_layout).setSelected(false);
                getView(R.id.bt_auto_wiper_layout).setSelected(false);
            }else if(carInfoDriver.getWiper() == 1){
                getView(R.id.bt_wiperint_gear_layout).setSelected(true);
                getView(R.id.bt_wiperlo_gear_layout).setSelected(false);
                getView(R.id.bt_wiperhi_gear_layout).setSelected(false);
                getView(R.id.bt_auto_wiper_layout).setSelected(false);
            }else if(carInfoDriver.getWiper() == 2){
                getView(R.id.bt_wiperint_gear_layout).setSelected(false);
                getView(R.id.bt_wiperlo_gear_layout).setSelected(true);
                getView(R.id.bt_wiperhi_gear_layout).setSelected(false);
                getView(R.id.bt_auto_wiper_layout).setSelected(false);
            }else if(carInfoDriver.getWiper() == 3){
                getView(R.id.bt_wiperint_gear_layout).setSelected(false);
                getView(R.id.bt_wiperlo_gear_layout).setSelected(false);
                getView(R.id.bt_wiperhi_gear_layout).setSelected(true);
                getView(R.id.bt_auto_wiper_layout).setSelected(false);
            }else if(carInfoDriver.getWiper() == 4){
                getView(R.id.bt_wiperint_gear_layout).setSelected(false);
                getView(R.id.bt_wiperlo_gear_layout).setSelected(false);
                getView(R.id.bt_wiperhi_gear_layout).setSelected(false);
                getView(R.id.bt_auto_wiper_layout).setSelected(true);
            }

            if(carInfoDriver.getSpray() == 0){
                getView(R.id.bt_spray_layout).setSelected(false);
            }else{
                getView(R.id.bt_spray_layout).setSelected(true);
            }

            if(carInfoDriver.getAutolight() == 0){
                getView(R.id.bt_autolight_layout).setSelected(false);
            }else{
                getView(R.id.bt_autolight_layout).setSelected(true);
            }
        } else {
            Log.e("KDSERVICE", "CarCtrlFragment.refreshPannelStatus() service is null");
        }
    }

    private class BaseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Set<String> keySet = bundle.keySet();
            for(String key : keySet) {

                if(key.compareTo("KD_CAST_EVENT0") != 0) {
                    continue;
                }
                if(bundle.getBoolean(key)) {
//                    myHandler.sendEmptyMessage(AIR_DISABLED);
                } else {
                    myHandler.sendEmptyMessage(UPDATE_PANNEL);
                }
            }
//            refreshWheels();
        }
    }

}
