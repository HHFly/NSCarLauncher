package com.kandi.dell.nscarlauncher.ui.setting.fragment;

import android.os.SystemProperties;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.setting.SetFragment;

public class AboutFragment extends BaseFragment {
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_about;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
            setClickListener(R.id.iv_return);
    }

    @Override
    public void initView() {
        setTvText(R.id.about_4,android.os.Build.VERSION.RELEASE);
        setTvText(R.id.about_1,android.os.Build.BOARD);
        setTvText(R.id.about_2,android.os.Build.VERSION.RELEASE+"  "+android.os.Build.VERSION.CODENAME );
        setTvText(R.id.about_3,android.os.Build.DEVICE+"  "+android.os.Build.DISPLAY);

        setTvText(R.id.about_5, android.os.Build.MODEL);
        setTvText(R.id.about_6, SystemProperties.get("ro.baseband"));//基频版本
        setTvText(R.id.about_7, SystemProperties.get("ro.qiyang.sys_version") );//核心版本
        setTvText(R.id.about_8, android.os.Build.DISPLAY);
        setTvText(R.id.about_9,SystemProperties.get("ro.build.date"));
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_return:
                    SetFragment.hideFragment();
                    break;
            }
    }
}
