package com.kandi.dell.nscarlauncher.ui_portrait.carctrl;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;

public class CarCtrlFragment extends BaseFragment{
    private HomePagerActivity homePagerActivity;

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.CARCONTROLL);
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public void onUnFirstResume() {

    }

    @Override
    public void Resume() {
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
        setClickListener(R.id.bt_back);
        setClickListener(R.id.bt_car_near_light_layout);
        setClickListener(R.id.bt_car_far_light_layout);
        setClickListener(R.id.bt_car_alert_light_layout);
        setClickListener(R.id.bt_car_backfog_light_layout);
        setClickListener(R.id.bt_car_door_layout);
        setClickListener(R.id.bt_car_win_layout);
        setClickListener(R.id.bt_car_trunk_layout);
        setClickListener(R.id.bt_car_lightoff_layout);
    }

    @Override
    public void initView() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_back:
                homePagerActivity.hideFragment();
                break;
            case R.id.bt_car_near_light_layout:
                selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,true);
                selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,false);
                //调用can接口发送报文

                //刷新界面
                isShowView(R.id.car_near_light,true);
                isShowView(R.id.car_far_light,false);
                break;
            case R.id.bt_car_far_light_layout:
                selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,false);
                selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,true);
                //调用can接口发送报文

                //刷新界面
                isShowView(R.id.car_near_light,false);
                isShowView(R.id.car_far_light,true);
                break;
            case R.id.bt_car_alert_light_layout:
                boolean car_alert_light_status = getView(R.id.bt_car_alert_light_layout).isSelected();
                selectBtView(R.id.bt_car_alert_light_layout,R.id.bt_car_alert_light_img,R.id.bt_car_alert_light_txt,!car_alert_light_status);
                //调用can接口发送报文

                //刷新界面
                isShowView(R.id.car_alert_light,!car_alert_light_status);
                break;
            case R.id.bt_car_backfog_light_layout:
                boolean car_backfog_light_status = getView(R.id.bt_car_backfog_light_layout).isSelected();
                selectBtView(R.id.bt_car_backfog_light_layout,R.id.bt_car_backfog_light_img,R.id.bt_car_backfog_light_txt,!car_backfog_light_status);
                //调用can接口发送报文

                //刷新界面
                isShowView(R.id.car_fog_light,!car_backfog_light_status);
                break;
            case R.id.bt_car_door_layout:
                boolean car_door_status = getView(R.id.bt_car_door_layout).isSelected();
                selectBtView(R.id.bt_car_door_layout,R.id.bt_car_door_img,R.id.bt_car_door_txt,!car_door_status);
                //调用can接口发送报文

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
                break;
            case R.id.bt_car_win_layout:
                boolean car_win_status = getView(R.id.bt_car_win_layout).isSelected();
                selectBtView(R.id.bt_car_win_layout,R.id.bt_car_win_img,R.id.bt_car_win_txt,!car_win_status);
                //调用can接口发送报文

                //刷新界面
                isShowView(R.id.car_left_win_down,!car_win_status);
                isShowView(R.id.car_right_win_down,!car_win_status);
                break;
            case R.id.bt_car_trunk_layout:
                selectBtView(R.id.bt_car_trunk_layout,R.id.bt_car_trunk_img,R.id.bt_car_trunk_txt,true);
                //调用can接口发送报文

                //刷新界面
                isShowView(R.id.car_trunk_lock_on,true);
                isShowView(R.id.car_trunk_lock_off,false);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isShowView(R.id.car_trunk_lock_on,false);
                    }
                },1500);
                break;
            case R.id.bt_car_lightoff_layout:
                //selectBtView(R.id.bt_car_lightoff_layout,R.id.bt_car_lightoff_img,R.id.bt_car_lightoff_txt,true);
                selectBtView(R.id.bt_car_near_light_layout,R.id.bt_car_near_light_img,R.id.bt_car_near_light_txt,false);
                selectBtView(R.id.bt_car_far_light_layout,R.id.bt_car_far_light_img,R.id.bt_car_far_light_txt,false);
                selectBtView(R.id.bt_car_alert_light_layout,R.id.bt_car_alert_light_img,R.id.bt_car_alert_light_txt,false);
                selectBtView(R.id.bt_car_backfog_light_layout,R.id.bt_car_backfog_light_img,R.id.bt_car_backfog_light_txt,false);
                //调用can接口发送报文

                //刷新界面
                isShowView(R.id.car_near_light,false);
                isShowView(R.id.car_far_light,false);
                isShowView(R.id.car_alert_light,false);
                isShowView(R.id.car_fog_light,false);
                break;
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

    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    break;
            }
        };
    };

}
