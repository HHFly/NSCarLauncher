package com.kandi.dell.nscarlauncher.ui_portrait.airctrl;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;

public class AirCtrlFragment extends BaseFragment{
    private HomePagerActivity homePagerActivity;

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.AIRCONTROLL);
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
        return R.layout.fragment_airctrl;
    }

    @Override
    public void findView() {
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
            case R.id.bt_airctrl_off:
                boolean airctrl_off_status = getView(R.id.bt_airctrl_off).isSelected();
                selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,!airctrl_off_status);
                if(!airctrl_off_status){
                    selectBtView(R.id.bt_airctrl_ac,R.id.bt_airctrl_ac_img,R.id.bt_airctrl_ac_txt,false);
                    selectBtView(R.id.bt_airctrl_ptc,R.id.bt_airctrl_ptc_img,R.id.bt_airctrl_ptc_txt,false);
                }
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_ac:
                if(getView(R.id.bt_airctrl_off).isSelected()){
                    selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,false);
                }
                selectBtView(R.id.bt_airctrl_ac,R.id.bt_airctrl_ac_img,R.id.bt_airctrl_ac_txt,!getView(R.id.bt_airctrl_ac).isSelected());
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_ptc:
                if(getView(R.id.bt_airctrl_off).isSelected()){
                    selectBtView(R.id.bt_airctrl_off,R.id.bt_airctrl_off_img,R.id.bt_airctrl_off_txt,false);
                }
                selectBtView(R.id.bt_airctrl_ptc,R.id.bt_airctrl_ptc_img,R.id.bt_airctrl_ptc_txt,!getView(R.id.bt_airctrl_ptc).isSelected());
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_incycle:
                selectBtView(R.id.bt_airctrl_incycle,R.id.bt_airctrl_incycle_img,R.id.bt_airctrl_incycle_txt,true);
                selectBtView(R.id.bt_airctrl_outcycle,R.id.bt_airctrl_outcycle_img,R.id.bt_airctrl_outcycle_txt,false);
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_outcycle:
                selectBtView(R.id.bt_airctrl_incycle,R.id.bt_airctrl_incycle_img,R.id.bt_airctrl_incycle_txt,false);
                selectBtView(R.id.bt_airctrl_outcycle,R.id.bt_airctrl_outcycle_img,R.id.bt_airctrl_outcycle_txt,true);
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_head:
                selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,true);
                selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,false);
                selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,false);
                selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,false);
                selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,false);
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_foot:
                selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,false);
                selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,true);
                selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,false);
                selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,false);
                selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,false);
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_head_foot:
                selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,false);
                selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,false);
                selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,true);
                selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,false);
                selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,false);
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_foot_win:
                selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,false);
                selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,false);
                selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,false);
                selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,true);
                selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,false);
                //调用can接口发送报文
                break;
            case R.id.bt_airctrl_defog:
                selectBtView(R.id.bt_airctrl_head,R.id.bt_airctrl_head_img,R.id.bt_airctrl_head_txt,false);
                selectBtView(R.id.bt_airctrl_foot,R.id.bt_airctrl_foot_img,R.id.bt_airctrl_foot_txt,false);
                selectBtView(R.id.bt_airctrl_head_foot,R.id.bt_airctrl_head_foot_img,R.id.bt_airctrl_head_foot_txt,false);
                selectBtView(R.id.bt_airctrl_foot_win,R.id.bt_airctrl_foot_win_img,R.id.bt_airctrl_foot_win_txt,false);
                selectBtView(R.id.bt_airctrl_defog,R.id.bt_airctrl_defog_img,R.id.bt_airctrl_defog_txt,true);
                //调用can接口发送报文
                break;
        }
    }

    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

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

}
