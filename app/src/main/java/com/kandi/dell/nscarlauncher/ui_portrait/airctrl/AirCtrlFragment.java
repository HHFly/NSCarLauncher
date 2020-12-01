package com.kandi.dell.nscarlauncher.ui_portrait.airctrl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.candriver.AirConditionDriver;
import com.kandi.dell.nscarlauncher.candriver.DriverServiceManger;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.candriver.AirConditionDriver.eAirBlowMode;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class AirCtrlFragment extends BaseFragment{
    private HomePagerActivity homePagerActivity;
    AirConditionDriver airPannelDrv;
    private TextView air_temp;
    BaseReceiver baseReceiver;
    boolean isbreak = false;
    Thread thread;
    private final int UPDATE_PANNEL = 1;
    int presetTemp,windSpeed;
    boolean isUserClick = false;//用户操作延迟刷新

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.AIRCONTROLL);
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_airctrl;
    }

    @Override
    public void findView() {
        air_temp = getView(R.id.air_temp);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
        setClickListener(R.id.bt_airctrl_off);
        setClickListener(R.id.bt_airctrl_ac);
        setClickListener(R.id.bt_airctrl_ptc);
        setClickListener(R.id.bt_airctrl_incycle);
        setClickListener(R.id.bt_airctrl_outcycle);
        setClickListener(R.id.bt_airctrl_head);
        setClickListener(R.id.bt_airctrl_foot);
        setClickListener(R.id.bt_airctrl_head_foot);
        setClickListener(R.id.bt_airctrl_foot_win);
        setClickListener(R.id.bt_airctrl_defog);
        setClickListener(R.id.set_btn_temp_plus);
        setClickListener(R.id.set_btn_temp_min);
        setClickListener(R.id.set_btn_speed_plus);
        setClickListener(R.id.set_btn_speed_min);
    }

    @Override
    public void initView() {
        setmType(FragmentType.AIRCONTROLL);
        if(baseReceiver == null){
            baseReceiver = new BaseReceiver();
            IntentFilter kdIntentFilter = new IntentFilter();
            kdIntentFilter.addAction("com.driverlayer.kdos_driverserver");
            getActivity().registerReceiver(baseReceiver, kdIntentFilter);
        }
        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
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
        setViewVisibility(R.id.loadingView,false);
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
        thread.setName("EnergyCycleFragment");
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
        try {
            isUserClick = true;
            switch (view.getId()){
                case R.id.bt_back:
                    homePagerActivity.hideFragment();
                    break;
                case R.id.bt_airctrl_off:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    boolean airctrl_off_status = getView(R.id.bt_airctrl_off).isSelected();
                    selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,!airctrl_off_status);
                    if(!airctrl_off_status){
                        setTvText(R.id.bt_airctrl_off_txt,R.string.ON);
                    }else{
                        selectBtView(R.id.bt_airctrl_ac,R.id.bt_airctrl_ac_img,R.id.bt_airctrl_ac_txt,false);
                        selectBtView(R.id.bt_airctrl_ptc,R.id.bt_airctrl_ptc_img,R.id.bt_airctrl_ptc_txt,false);
                        setTvText(R.id.bt_airctrl_off_txt,R.string.OFF);
                        airPannelDrv.isACPowerOn = false;
                        airPannelDrv.isPTCPowerOn = false;
                    }
                    //调用can接口发送报文
                    airPannelDrv.setWindPowerOn(!airctrl_off_status);
                    break;
                case R.id.bt_airctrl_ac:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    if(!getView(R.id.bt_airctrl_off).isSelected()){
                        selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,true);
                        setTvText(R.id.bt_airctrl_off_txt,R.string.ON);
                        airPannelDrv.isWindPowerOn = true;
                    }
                    boolean acStatus = !getView(R.id.bt_airctrl_ac).isSelected();
                    selectBtView(R.id.bt_airctrl_ac,R.id.bt_airctrl_ac_img,R.id.bt_airctrl_ac_txt,acStatus);
                    //调用can接口发送报文
                    airPannelDrv.setACPowerOn(acStatus);
                    break;
                case R.id.bt_airctrl_ptc:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    if(!getView(R.id.bt_airctrl_off).isSelected()){
                        selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,true);
                        setTvText(R.id.bt_airctrl_off_txt,R.string.ON);
                        airPannelDrv.isWindPowerOn = true;
                    }
                    boolean ptcStatus = !getView(R.id.bt_airctrl_ptc).isSelected();
                    selectBtView(R.id.bt_airctrl_ptc,R.id.bt_airctrl_ptc_img,R.id.bt_airctrl_ptc_txt,ptcStatus);
                    //调用can接口发送报文
                    airPannelDrv.setPtcPowerOn(ptcStatus);
                    break;
                case R.id.bt_airctrl_incycle:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    selectBtView(R.id.bt_airctrl_incycle,R.id.bt_airctrl_incycle_img,R.id.bt_airctrl_incycle_txt,true);
                    selectBtView(R.id.bt_airctrl_outcycle,R.id.bt_airctrl_outcycle_img,R.id.bt_airctrl_outcycle_txt,false);
                    //调用can接口发送报文
                    airPannelDrv.setInternalCycle(true);
                    break;
                case R.id.bt_airctrl_outcycle:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    selectBtView(R.id.bt_airctrl_incycle,R.id.bt_airctrl_incycle_img,R.id.bt_airctrl_incycle_txt,false);
                    selectBtView(R.id.bt_airctrl_outcycle,R.id.bt_airctrl_outcycle_img,R.id.bt_airctrl_outcycle_txt,true);
                    //调用can接口发送报文
                    airPannelDrv.setInternalCycle(false);
                    break;
                case R.id.bt_airctrl_head:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,true);
                    selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,false);
                    selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,false);
                    selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,false);
                    selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,false);
                    //调用can接口发送报文
                    airPannelDrv.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_HEAD);
                    break;
                case R.id.bt_airctrl_foot:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,false);
                    selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,true);
                    selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,false);
                    selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,false);
                    selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,false);
                    //调用can接口发送报文
                    airPannelDrv.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_FOOT);
                    break;
                case R.id.bt_airctrl_head_foot:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,false);
                    selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,false);
                    selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,true);
                    selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,false);
                    selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,false);
                    //调用can接口发送报文
                    airPannelDrv.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_HEAD_FOOT);
                    break;
                case R.id.bt_airctrl_foot_win:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,false);
                    selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,false);
                    selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,false);
                    selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,true);
                    selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,false);
                    //调用can接口发送报文
                    airPannelDrv.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_FOOT_DEMIST);
                    break;
                case R.id.bt_airctrl_defog:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,false);
                    selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,false);
                    selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,false);
                    selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,false);
                    selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,true);
                    //调用can接口发送报文
                    airPannelDrv.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_DEMIST);
                    break;
                case R.id.set_btn_temp_plus:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    presetTemp = Integer.parseInt(getTvText(R.id.set_temp));
                    presetTemp = presetTemp+1>=32?32:presetTemp+1;
                    setTvText(R.id.set_temp,""+presetTemp);
                    airPannelDrv.setPresetTemp(presetTemp);
                    break;
                case R.id.set_btn_temp_min:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    presetTemp = Integer.parseInt(getTvText(R.id.set_temp));
                    presetTemp = presetTemp-1<=18?18:presetTemp-1;
                    setTvText(R.id.set_temp,""+presetTemp);
                    airPannelDrv.setPresetTemp(presetTemp);
                    break;
                case R.id.set_btn_speed_plus:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    windSpeed = Integer.parseInt(getTvText(R.id.set_speed));
                    windSpeed = windSpeed+1>=8?8:windSpeed+1;
                    setTvText(R.id.set_speed,""+windSpeed);
                    selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,true);
                    setTvText(R.id.bt_airctrl_off_txt,R.string.ON);
                    airPannelDrv.setWindSpeed(windSpeed);
                    break;
                case R.id.set_btn_speed_min:
                    if(airPannelDrv == null){
                        airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
                        return;
                    }
                    windSpeed = Integer.parseInt(getTvText(R.id.set_speed));
                    windSpeed = windSpeed-1<=1?1:windSpeed-1;
                    setTvText(R.id.set_speed,""+windSpeed);
                    selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,true);
                    setTvText(R.id.bt_airctrl_off_txt,R.string.ON);
                    airPannelDrv.setWindSpeed(windSpeed);
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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

    public void refreshACDisplay() {
        AirConditionDriver airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
        if(airPannelDrv != null) {

            int nInternalTemperature = airPannelDrv.getInsideTemp();

            air_temp.setText(nInternalTemperature);

            if(airPannelDrv.isPtcPowerOn() != getView(R.id.bt_airctrl_ptc).isSelected()) {
                selectBtView(R.id.bt_airctrl_ptc,R.id.bt_airctrl_ptc_img,R.id.bt_airctrl_ptc_txt,airPannelDrv.isPtcPowerOn());
            }
            if(airPannelDrv.isACPowerOn() != getView(R.id.bt_airctrl_ac).isSelected()) {
                selectBtView(R.id.bt_airctrl_ac,R.id.bt_airctrl_ac_img,R.id.bt_airctrl_ac_txt,airPannelDrv.isACPowerOn());
            }
            if(airPannelDrv.isWindPowerOn() != getView(R.id.bt_airctrl_off).isSelected()) {
                selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,airPannelDrv.isWindPowerOn());
                setTvText(R.id.bt_airctrl_off_txt,airPannelDrv.isWindPowerOn()?R.string.ON:R.string.OFF);
            }
        }
        else {
            Log.e("KDSERVICE", "AirConditionActivity.refreshPannelStatus() service is null");
        }
    }

    public void refreshPannelStatus() {
        AirConditionDriver airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();

        if(airPannelDrv != null) {
            try {
                int ret = airPannelDrv.retreveACInfo();
                if(ret == 1){
                    setViewVisibility(R.id.loadingView,true);
                    return;
                } else {
                    setViewVisibility(R.id.loadingView,false);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if(airPannelDrv.isPtcPowerOn() != getView(R.id.bt_airctrl_ptc).isSelected()) {
                selectBtView(R.id.bt_airctrl_ptc,R.id.bt_airctrl_ptc_img,R.id.bt_airctrl_ptc_txt,airPannelDrv.isPtcPowerOn());
            }
            if(airPannelDrv.isACPowerOn() != getView(R.id.bt_airctrl_ac).isSelected()) {
                selectBtView(R.id.bt_airctrl_ac,R.id.bt_airctrl_ac_img,R.id.bt_airctrl_ac_txt,airPannelDrv.isACPowerOn());
            }
            if(airPannelDrv.isWindPowerOn() != getView(R.id.bt_airctrl_off).isSelected()) {
                selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,airPannelDrv.isWindPowerOn());
                setTvText(R.id.bt_airctrl_off_txt,airPannelDrv.isWindPowerOn()?R.string.ON:R.string.OFF);
            }

            selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,airPannelDrv.getAirBlowMode()== eAirBlowMode.BLOW_HEAD);
            selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,airPannelDrv.getAirBlowMode()== eAirBlowMode.BLOW_FOOT);
            selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,airPannelDrv.getAirBlowMode()== eAirBlowMode.BLOW_HEAD_FOOT);
            selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,airPannelDrv.getAirBlowMode()== eAirBlowMode.BLOW_FOOT_DEMIST);
            selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,airPannelDrv.getAirBlowMode()== eAirBlowMode.BLOW_DEMIST);

            selectBtView(R.id.bt_airctrl_incycle,R.id.bt_airctrl_incycle_img,R.id.bt_airctrl_incycle_txt,airPannelDrv.isInternalCycle());
            selectBtView(R.id.bt_airctrl_outcycle,R.id.bt_airctrl_outcycle_img,R.id.bt_airctrl_outcycle_txt,!airPannelDrv.isInternalCycle());

            //refresh temperature display
            int nInternalTemperature = airPannelDrv.getInsideTemp();
            air_temp.setText(nInternalTemperature+"℃");

            setTvText(R.id.set_temp,airPannelDrv.getPresetTemp() == 0?""+25:""+airPannelDrv.getPresetTemp());
            setTvText(R.id.set_speed,""+airPannelDrv.getWindSpeed());

            Log.d("ACCondition", "PTC="+ airPannelDrv.isPtcPowerOn()+", AC="+airPannelDrv.isACPowerOn()+", PWR="+ airPannelDrv.isWindPowerOn()+
                    ", mode="+airPannelDrv.getAirBlowMode()+", cyc="+airPannelDrv.isInternalCycle()+
                    ", setT="+airPannelDrv.getPresetTemp()+", setWind="+airPannelDrv.getWindSpeed()+
                    ", temp="+airPannelDrv.getInsideTemp());

        }
        else {
            Log.e("KDSERVICE", "AirConditionActivity.refreshPannelStatus() service is null");
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
