package com.example.dell.nscarlauncher.ui.setting.fragment;

import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.setting.SetFragment;

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
        setTvText(R.id.about_1,android.os.Build.BOARD);
        setTvText(R.id.about_2,android.os.Build.MODEL );
        setTvText(R.id.about_1,android.os.Build.DISPLAY);
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
