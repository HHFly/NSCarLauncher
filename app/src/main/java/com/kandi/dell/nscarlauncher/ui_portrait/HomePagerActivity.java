package com.kandi.dell.nscarlauncher.ui_portrait;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.Activity.BaseActivity;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.common.util.IsHomeUtils;
import com.kandi.dell.nscarlauncher.common.util.TimeUtils;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;

public class HomePagerActivity extends BaseActivity {
    public BaseFragment mCurFragment;//当前页
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_portrait;
    }

    @Override
    public void initView() {
        createFragment();
    }
    @Override
    public void setListener() {
        setClickListener(R.id.iv_music);
        setClickListener(R.id.item_air);
        setClickListener(R.id.item_app);
        setClickListener(R.id.item_phone);
        setClickListener(R.id.item_power);
        setClickListener(R.id.item_set);
        setClickListener(R.id.item_fm);
        setClickListener(R.id.item_btmusic);
        setClickListener(R.id.item_carcontroll);
    }

    @Override
    public void onClick(View v) {
        if (TimeUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()){
            case R.id.iv_music:
                break;
            case R.id.item_air:
                break;
            case R.id.item_app:
                break;
            case R.id.item_phone:
                break;
            case R.id.item_power:
                break;
            case R.id.item_set:
                jumpFragment(FragmentType.SET);
                break;
            case R.id.item_fm:
                break;
            case R.id.item_btmusic:
                break;
            case R.id.item_carcontroll:
                break;
        }
    }
    /**
     * 初始化fragment
     */
    private void createFragment() {
        App.get().setmCurActivity(this);
    }
    public Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case HandleKey.SHOW:
                        showFragemnt();

                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        };
    };
    public void jumpFragment(@FragmentType int type ){
        if(IsHomeUtils.isForeground(this,"HomePagerActivity"))
            switch (type) {

                case FragmentType.SET:
//                    switchFragment(setFragment);
                    break;

            }
    }
    /**
     * 选择Fragment
     *
     * @param fragment
     */

    private void switchFragment(BaseFragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(this, mCurFragment, fragment, R.id.frame_main);

        mCurFragment.Resume();

        myHandler.sendMessage(myHandler.obtainMessage(HandleKey.SHOW));
    }
    public  void showFragemnt(){
        setViewVisibility(R.id.frame_main,true);
    }
    /*隐藏fragemt*/
    public  void  hideFragment() {
        setViewVisibility(R.id.frame_main, false);
        hideLoadingDialog();
    }
}
