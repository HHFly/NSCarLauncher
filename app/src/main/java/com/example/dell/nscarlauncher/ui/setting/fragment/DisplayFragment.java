package com.example.dell.nscarlauncher.ui.setting.fragment;

import android.view.View;
import android.widget.SeekBar;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.ScreenManager;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.example.dell.nscarlauncher.ui.setting.SetFragment;

public class DisplayFragment extends BaseFragment {
    SeekBar seekBar ;
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_display;
    }

    @Override
    public void findView() {
        seekBar =getView(R.id.seekbar_display);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int ScreenBrightness = (int) Math.round((float)progress/100.0  * 255);
                ScreenManager.setScreenBrightness(ScreenBrightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void initView() {
        int  ScreenBrightness = ScreenManager.getScreenBrightness();
        int progress = Math.round(ScreenBrightness* 100 /255);
        seekBar.setProgress(progress);
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
