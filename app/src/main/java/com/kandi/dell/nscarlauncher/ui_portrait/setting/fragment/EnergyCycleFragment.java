package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.candriver.CarInfoDriver;
import com.kandi.dell.nscarlauncher.candriver.DriverServiceManger;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.CarModeAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class EnergyCycleFragment extends BaseFragment  implements CompoundButton.OnCheckedChangeListener{
    private HomePagerActivity homePagerActivity;
    private ImageView set_low,set_middle,set_high;
    private SwitchCompat is_cycle_open;
    CarInfoDriver carInfoDriver;
    boolean isbreak = false;
    Thread thread;
    private final int UPDATE_PANNEL = 1;
    int[] param = new int[2];
    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_energycycle_portrait;
    }

    @Override
    public void findView() {
        is_cycle_open = getView(R.id.is_cycle_open);
        set_low = getView(R.id.set_low);
        set_middle = getView(R.id.set_middle);
        set_high = getView(R.id.set_high);
    }

    @Override
    public void setListener() {
            is_cycle_open.setOnCheckedChangeListener(this);
            setClickListener(R.id.bt_back);
            setClickListener(R.id.set_low);
            setClickListener(R.id.set_middle);
            setClickListener(R.id.set_high);
    }

    @Override
    public void initView() {

    }

    @Override
    public void onResume() {
        super.onResume();
        this.refreshPannelStatus();
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
    public void onClick(View v) {
        try{

            switch (v.getId()){
                case R.id.bt_back:
                    homePagerActivity.getSetFragment().hideFragment();
                    break;
                case R.id.set_low:
                    if(!is_cycle_open.isChecked()){
                        return;
                    }
                    set_low.setSelected(true);
                    set_middle.setSelected(false);
                    set_high.setSelected(false);
                    //调用can服务发送数据
                    if(carInfoDriver != null) {
                        carInfoDriver.getCar_SportEnergy();
                        param[0] = carInfoDriver.getCarMode();
                        param[1] = 0;
                        carInfoDriver.setCar_SportEnergy(param);
                    }
                    break;
                case R.id.set_middle:
                    if(!is_cycle_open.isChecked()){
                        return;
                    }
                    set_low.setSelected(false);
                    set_middle.setSelected(true);
                    set_high.setSelected(false);
                    //调用can服务发送数据
                    if(carInfoDriver != null) {
                        carInfoDriver.getCar_SportEnergy();
                        param[0] = carInfoDriver.getCarMode();
                        param[1] = 1;
                        carInfoDriver.setCar_SportEnergy(param);
                    }
                    break;
                case R.id.set_high:
                    if(!is_cycle_open.isChecked()){
                        return;
                    }
                    set_low.setSelected(false);
                    set_middle.setSelected(false);
                    set_high.setSelected(true);
                    //调用can服务发送数据
                    if(carInfoDriver != null) {
                        carInfoDriver.getCar_SportEnergy();
                        param[0] = carInfoDriver.getCarMode();
                        param[1] = 2;
                        carInfoDriver.setCar_SportEnergy(param);
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        try{
            switch (buttonView.getId()){
                case R.id.isopen:
                    if(isChecked){
                        if(carInfoDriver != null){
                            carInfoDriver.getCar_SportEnergy();
                            param[0] = carInfoDriver.getCarMode();
                            if(set_low.isSelected()){
                                param[1] = 0;
                            }
                            if(set_middle.isSelected()){
                                param[1] = 1;
                            }
                            if(set_high.isSelected()){
                                param[1] = 2;
                            }
                            set_middle.setSelected(true);
                            carInfoDriver.setCar_SportEnergy(param);
                        }
                        getView(R.id.eco_cycle_layout).setVisibility(View.VISIBLE);
                    }else {
                        if(carInfoDriver != null){
                            param[0] = 1;
                            param[1] = 3;
                            carInfoDriver.setCar_SportEnergy(param);
                        }
                        getView(R.id.eco_cycle_layout).setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PANNEL:
                    refreshPannelStatus();
                    break;
                default:
                    break;
            }
        };
    };

    public void refreshPannelStatus(){
        carInfoDriver = DriverServiceManger.getInstance().getCarInfoDriver();
        try{
            if(carInfoDriver != null) {
                carInfoDriver.getCar_SportEnergy();
                is_cycle_open.setChecked(carInfoDriver.getEnergyLevel()!=3);
                set_low.setSelected(carInfoDriver.getEnergyLevel() == 0);
                set_middle.setSelected(carInfoDriver.getEnergyLevel() == 1);
                set_high.setSelected(carInfoDriver.getEnergyLevel() == 2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
