package com.kandi.dell.nscarlauncher.ui.setting;


import android.content.Intent;
import android.provider.Settings;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.common.util.JumpUtils;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.setting.adapter.SetAdapter;
import com.kandi.dell.nscarlauncher.ui.setting.eumn.SetType;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.AboutFragment;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.BlueToothSetFragment;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.DateFragment;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.DisplayFragment;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.EqFragment;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.LanguageFragment;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.WifiFragment;
import com.kandi.dell.nscarlauncher.ui.setting.model.SetData;
import com.kandi.dell.nscarlauncher.ui.setting.model.SetModel;

import java.util.List;


public class SetFragment extends BaseFragment {
    private SetData  mData =new SetData();
    private SetAdapter mAdapter;
    private BaseFragment mCurFragment;//当前页
    private static RelativeLayout fragmentShow;
    BlueToothSetFragment blueToothSetFragment;
    WifiFragment wifiFragment;
    DisplayFragment displayFragment;
    DateFragment dateFragment;
    LanguageFragment languageFragment;
    AboutFragment aboutFragment;
    EqFragment eqFragment;
    @Override
    public int getContentResId() {
        return R.layout.fragment_set;
    }

    @Override
    public void findView() {
        fragmentShow=getView(R.id.rl_f_set);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.SET);
    }

    @Override
    public void initView() {
         blueToothSetFragment =new BlueToothSetFragment();
         wifiFragment =new WifiFragment();
        displayFragment =new DisplayFragment();
        dateFragment =new DateFragment();
        languageFragment =new LanguageFragment();
        aboutFragment =new AboutFragment();
        eqFragment= new EqFragment();
         initRvAdapter(mData.getData());
    }

    @Override
    public void onClick(View v) {

    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<SetModel> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView);
            mAdapter =new SetAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new SetAdapter.OnItemClickListener() {

                @Override
                public void onClickData(SetModel data) {
                    Click(data);
                }

            });

        }else {
            mAdapter.notifyDataSetChanged();
        }

    }
    /**
     * 选择Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(getActivity(), mCurFragment, fragment, R.id.frame_set);
        setVisibilityGone(R.id.rl_f_set,true);
    }
    public  static void hideFragment(){
        fragmentShow.setVisibility(View.GONE);
    }
    private void Click(SetModel data){
            switch (data.getItem()){
                case SetType.DISPLAY:
                    switchFragment(displayFragment);
                    break;
                case  SetType.DATE:
//                    switchFragment(dateFragment);
                    startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                    break;
                case  SetType.LANGUAGE:
//                    JumpUtils.actActivity(getActivity(),Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);
                    JumpUtils.actComponenActivity(getActivity(),"com.android.settings","com.android.settings.LanguageSettings");
//                    switchFragment(languageFragment);
                    break;
                case  SetType.ABOUT:
                    switchFragment(aboutFragment);
//                    startActivity(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS));
//                    JumpUtils.actComponenActivity(getActivity(),"com.android.settings","com.android.settings.Settings$DeviceInfoSettingsActivity");
//                    JumpUtils.actActivity(getActivity(),Settings.ACTION_DEVICE_INFO_SETTINGS);
                    break;
                case SetType.BT:
                    switchFragment(blueToothSetFragment);
//                    JumpUtils.actActivity(getActivity(),BlueToothSetFragment.class);
                    break;
                 case  SetType.WIFI:
//                     switchFragment(wifiFragment);
                     startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
//                     JumpUtils.actActivity(getActivity(), WifiFragment.class);
                     break;
                case  SetType.CARSET:
                    JumpUtils.actAPK(getActivity(), FragmentType.CARSET);
                    break;
                case  SetType.RECOVERY:
                    JumpUtils.actAPK(getActivity(), FragmentType.POWERRECOVER);
                    break;
                    case SetType.EQULIZER:
                        switchFragment(eqFragment);
                        break;

            }
    }
}
