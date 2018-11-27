package com.kandi.dell.nscarlauncher.base.fragment;

import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;

public class NullFragment extends BaseFragment {
    public int originId = 0;//来源与那个fragment用于隐藏蓝牙设置界面
    public int getOriginId(){
        return originId;
    }
    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.BTSET);
    }
    @Override
    public int getContentResId() {
        return R.layout.activity_btset;
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                if (getOriginId() == 0) {
                    HomePagerActivity.homePagerActivity.getSetFragment().hideFragment();
                } else if (getOriginId() == 1) {
                    HomePagerActivity.homePagerActivity.isShowPhoneAnim = false;
                    HomePagerActivity.homePagerActivity.jumpFragment(FragmentType.BTMUSIC);

                } else {
                    HomePagerActivity.homePagerActivity.isShowPhoneAnim = false;
                    HomePagerActivity.homePagerActivity.jumpFragment(FragmentType.PHONE);

                }
                break;
        }
    }
}
