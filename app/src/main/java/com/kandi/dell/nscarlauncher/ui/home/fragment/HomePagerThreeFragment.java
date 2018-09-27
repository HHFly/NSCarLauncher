package com.kandi.dell.nscarlauncher.ui.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.JumpUtils;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;

public class HomePagerThreeFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }


    @Override
    public int getContentResId() {
        return R.layout.fragment_home3;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
            setClickListener(R.id.rl_set);
            setClickListener(R.id.rl_app);
            setClickListener(R.id.rl_video);
    }

    @Override
    public void initView() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.rl_set:
                HomePagerActivity.jumpFragment(FragmentType.SET);
                break;
            case R.id.rl_app:
                HomePagerActivity.jumpFragment(FragmentType.APPLICATION);
                break;
            case R.id.rl_video:
                HomePagerActivity.jumpFragment(FragmentType.VIDEO);
                break;
        }
    }


}
