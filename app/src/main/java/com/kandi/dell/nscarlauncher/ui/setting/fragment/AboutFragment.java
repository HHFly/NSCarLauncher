package com.kandi.dell.nscarlauncher.ui.setting.fragment;

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
