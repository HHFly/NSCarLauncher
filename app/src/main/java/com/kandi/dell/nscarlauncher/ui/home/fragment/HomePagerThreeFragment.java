package com.kandi.dell.nscarlauncher.ui.home.fragment;

import android.view.View;
import android.widget.LinearLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;

public class HomePagerThreeFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    private LinearLayout layout_home;
    public String PicIndex ="picindex";

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }


    @Override
    public int getContentResId() {
        return R.layout.fragment_home3;
    }

    @Override
    public void findView() {
        layout_home = getView(R.id.layout_home);
    }

    @Override
    public void setListener() {
            setClickListener(R.id.rl_set);
            setClickListener(R.id.rl_app);
            setClickListener(R.id.rl_video);
    }

    @Override
    public void initView() {
        changBgView(SPUtil.getInstance(getContext(),PicIndex).getInt(PicIndex,0));
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

    /*设置背景param int resid*/
    public void changBgView(int resid){
        if(resid == 0){
            layout_home.setBackgroundResource(R.color.dfbackground);
        }else{
            layout_home.setBackgroundResource(resid);
        }
    }

}
