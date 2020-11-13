package com.kandi.dell.nscarlauncher.ui_portrait.setting;


import android.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.common.util.JumpUtils;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.SetAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.AboutFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.BlueToothSetFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.CarSetFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.DateFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.DisplayFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.EnergyCycleFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.EqFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.LanguageFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.UpgradeFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment.WifiFragment;

public class SetFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    private SetAdapter mAdapter;
    private BaseFragment mCurFragment;//当前页
    private RelativeLayout fragmentShow;
    BlueToothSetFragment blueToothSetFragment;
    WifiFragment wifiFragment;
    DisplayFragment displayFragment;
    DateFragment dateFragment;
    LanguageFragment languageFragment;
    AboutFragment aboutFragment;
    EqFragment eqFragment;
    UpgradeFragment upgradeFragment;
    CarSetFragment carSetFragment;
    EnergyCycleFragment energyCycleFragment;
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_portrait;
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public void findView() {
        fragmentShow=getView(R.id.rl_f_set);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
        setClickListener(R.id.display_layout);
        setClickListener(R.id.bt_layout);
        setClickListener(R.id.wifi_layout);
        setClickListener(R.id.language_layout);
        setClickListener(R.id.eq_layout);
        setClickListener(R.id.data_layout);
        setClickListener(R.id.carset_layout);
        setClickListener(R.id.energycycle_layout);
        setClickListener(R.id.update_layout);
        setClickListener(R.id.about_layout);
    }

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.SET);
    }

    @Override
    public void initView() {
        getDisplayFragment();
        getBlueToothSetFragment();
        getWifiFragment();
        getLanguageFragment();
        getEqFragment();
        getDateFragment();
        getCarSetFragment();
        getEnergyCycleFragment();
        getUpgradeFragment();
        getAboutFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_back:
                homePagerActivity.hideFragment();
                break;
            case R.id.display_layout:
                switchFragment(getDisplayFragment());
                break;
            case R.id.bt_layout:
                switchFragment(getBlueToothSetFragment());
                break;
            case R.id.wifi_layout:
                switchFragment(getWifiFragment());
//                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case R.id.language_layout:
                switchFragment(getLanguageFragment());
//                JumpUtils.actComponenActivity(getActivity(),"com.android.settings","com.android.settings.LanguageSettings");
                break;
            case R.id.eq_layout:
                switchFragment(getEqFragment());
                break;
            case R.id.data_layout:
                switchFragment(getDateFragment());
//                startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                break;
            case R.id.carset_layout:
//                JumpUtils.actAPK(getActivity(), FragmentType.CARSET);
                switchFragment(getCarSetFragment());
                break;
            case R.id.energycycle_layout:
                switchFragment(getEnergyCycleFragment());
//                JumpUtils.actAPK(getActivity(), FragmentType.POWERRECOVER);
                break;
            case R.id.update_layout:
                switchFragment(getUpgradeFragment());
                break;
            case R.id.about_layout:
                switchFragment(getAboutFragment());
                break;
        }
    }
    public void dissDialog(){
        if(blueToothSetFragment!=null)
        blueToothSetFragment.hideDialog();
    }
    /**
     * 选择Fragment
     *
     * @param fragment
     */
    private void switchFragment(BaseFragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(getActivity(), mCurFragment, fragment, R.id.frame_set);
        mCurFragment.Resume();
        setVisibilityGone(R.id.rl_f_set,true);
    }
    public void hideFragment(){
        if(mCurFragment != null){
            mCurFragment.Pause();
        }
        fragmentShow.setVisibility(View.GONE);
    }

    public DisplayFragment getDisplayFragment() {
        if(displayFragment == null){
            displayFragment = new DisplayFragment();
            displayFragment.setHomePagerActivity(homePagerActivity);
        }
        return displayFragment;
    }

    public BlueToothSetFragment getBlueToothSetFragment() {
        if(blueToothSetFragment == null){
            blueToothSetFragment = new BlueToothSetFragment();
            blueToothSetFragment.setHomePagerActivity(homePagerActivity);
        }
        return blueToothSetFragment;
    }

    public WifiFragment getWifiFragment() {
        if(wifiFragment == null){
            wifiFragment = new WifiFragment();
            wifiFragment.setHomePagerActivity(homePagerActivity);
        }
        return wifiFragment;
    }

    public LanguageFragment getLanguageFragment() {
        if(languageFragment == null){
            languageFragment = new LanguageFragment();
            languageFragment.setHomePagerActivity(homePagerActivity);
        }
        return languageFragment;
    }

    public EqFragment getEqFragment() {
        if(eqFragment == null){
            eqFragment = new EqFragment();
            eqFragment.setHomePagerActivity(homePagerActivity);
        }
        return eqFragment;
    }

    public DateFragment getDateFragment() {
        if(dateFragment == null){
            dateFragment = new DateFragment();
            dateFragment.setHomePagerActivity(homePagerActivity);
        }
        return dateFragment;
    }

    public UpgradeFragment getUpgradeFragment() {
        if(upgradeFragment == null){
            upgradeFragment = new UpgradeFragment();
            upgradeFragment.setHomePagerActivity(homePagerActivity);
        }
        return upgradeFragment;
    }

    public AboutFragment getAboutFragment() {
        if(aboutFragment == null){
            aboutFragment = new AboutFragment();
            aboutFragment.setHomePagerActivity(homePagerActivity);
        }
        return aboutFragment;
    }

    public CarSetFragment getCarSetFragment() {
        if(carSetFragment == null){
            carSetFragment = new CarSetFragment();
            carSetFragment.setHomePagerActivity(homePagerActivity);
        }
        return carSetFragment;
    }

    public EnergyCycleFragment getEnergyCycleFragment() {
        if(energyCycleFragment == null){
            energyCycleFragment = new EnergyCycleFragment();
            energyCycleFragment.setHomePagerActivity(homePagerActivity);
        }
        return energyCycleFragment;
    }
}
