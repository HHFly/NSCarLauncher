package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.candriver.CarInfoDriver;
import com.kandi.dell.nscarlauncher.candriver.DriverServiceManger;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;

public class CarSetFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    private LinearLayout car_choose_layout;
    boolean isbreak = false;
    Thread thread;
    private final int UPDATE_PANNEL = 1;
    private ImageView eco_mode_checkbox,sport_mode_checkbox;
    private CarInfoDriver carInfoDriver;
    int[] param = new int[2];
    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_car_portrait;
    }

    @Override
    public void findView() {
        car_choose_layout = getView(R.id.car_choose_layout);
        eco_mode_checkbox = getView(R.id.eco_mode_checkbox);
        sport_mode_checkbox = getView(R.id.sport_mode_checkbox);
    }

    @Override
    public void setListener() {
            setClickListener(R.id.bt_back);
            setClickListener(R.id.carset_layout);
            setClickListener(R.id.eco_mode_layout);
            setClickListener(R.id.sport_mode_layout);
    }

    @Override
    public void initView() {
        try {
            carInfoDriver = DriverServiceManger.getInstance().getCarInfoDriver();
            carInfoDriver.getCar_SportEnergy();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        try {
            switch (v.getId()){
                case R.id.bt_back:
                    homePagerActivity.getSetFragment().hideFragment();
                    break;
                case R.id.carset_layout:
                    car_choose_layout.setVisibility(car_choose_layout.getVisibility() == View.GONE?View.VISIBLE:View.GONE);
                    break;
                case R.id.eco_mode_layout:
                    eco_mode_checkbox.setSelected(true);
                    sport_mode_checkbox.setSelected(false);
                    if(carInfoDriver != null) {
                        carInfoDriver.getCar_SportEnergy();
                        param[0] = 1;
                        param[1] = carInfoDriver.getEnergyLevel();
                        carInfoDriver.setCar_SportEnergy(param);
                    }
                    break;
                case R.id.sport_mode_layout:
                    eco_mode_checkbox.setSelected(false);
                    sport_mode_checkbox.setSelected(true);
                    if(carInfoDriver != null) {
                        carInfoDriver.getCar_SportEnergy();
                        param[0] = 2;
                        param[1] = carInfoDriver.getEnergyLevel();
                        carInfoDriver.setCar_SportEnergy(param);
                    }
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
                    try {
                        if(carInfoDriver != null) {
                            carInfoDriver.getCar_SportEnergy();
                            eco_mode_checkbox.setSelected(carInfoDriver.getCarMode() == 1);
                            sport_mode_checkbox.setSelected(carInfoDriver.getCarMode() == 2);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        };
    };
}
