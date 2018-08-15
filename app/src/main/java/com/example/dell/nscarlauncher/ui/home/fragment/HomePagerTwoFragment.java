package com.example.dell.nscarlauncher.ui.home.fragment;

import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.example.dell.nscarlauncher.ui.home.androideunm.FragmentType;

public class HomePagerTwoFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    @Override
    public int getContentResId() {
        return R.layout.fragment_home2;
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
        setClickListener(R.id.rl_phone);
    }

    @Override
    public void initView() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_phone:
                if(homePagerActivity!=null){
                    homePagerActivity.jumpFragment(FragmentType.PHONE);
                }
                break;
        }
    }
}
