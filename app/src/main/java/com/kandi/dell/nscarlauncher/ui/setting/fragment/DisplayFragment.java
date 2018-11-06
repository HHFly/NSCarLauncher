package com.kandi.dell.nscarlauncher.ui.setting.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.common.util.LightnessControl;
import com.kandi.dell.nscarlauncher.common.util.ScreenManager;
import com.kandi.dell.nscarlauncher.ui.setting.SetFragment;

public class DisplayFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    SeekBar seekBar ;
    RelativeLayout setWallpaper;
    SwitchCompat autolight;
    private BaseFragment mCurFragment;//当前页
    private static RelativeLayout fragmentShow;
    SetWallpaperFragment setWallpaperFragment;

    @Override
    public int getContentResId() {
        return R.layout.fragment_set_display;
    }

    @Override
    public void findView() {
        seekBar = getView(R.id.seekbar_display);
        setWallpaper = getView(R.id.setWallpaper);
        setWallpaper.setVisibility(View.GONE);
        fragmentShow = getView(R.id.rl_set_wallpaper);
        autolight = getView(R.id.autolight);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        setClickListener(R.id.setWallpaper);
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
        setWallpaperFragment =new SetWallpaperFragment();
        int  ScreenBrightness = ScreenManager.getScreenBrightness();
        int progress = Math.round(ScreenBrightness* 100 /255);
        seekBar.setProgress(progress);
        autolight.setOnCheckedChangeListener(this);
        autolight.setChecked(LightnessControl.isAutoBrightness(getActivity()));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.autolight:
                if(isChecked){
                    LightnessControl.startAutoBrightness(getActivity());
                    seekBar.setEnabled(false);
                }else {
                    LightnessControl.stopAutoBrightness(getActivity());
                    seekBar.setEnabled(true);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 选择Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(getActivity(), mCurFragment, fragment, R.id.set_wallpaper);
        setVisibilityGone(R.id.rl_set_wallpaper,true);
    }

    public static void hideFragment(){
        fragmentShow.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                SetFragment.hideFragment();
                break;
            case R.id.setWallpaper:
                switchFragment(setWallpaperFragment);
                break;
        }
    }
}
