package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.content.Intent;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;

public class SetUserResetFragment extends BaseFragment {

    private HomePagerActivity homePagerActivity;

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_userreset_portrait;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        setClickListener(R.id.bt_resetcar);
    }

    @Override
    public void initView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                App.get().getCurActivity().getSetFragment().getUpgradeFragment().getSetResetFragment().hideFragment();
                break;
            case R.id.bt_resetcar:
                getContext().sendBroadcast(new Intent("com.kandi.MASTER_CLEAR"));
                break;
        }
    }
}
