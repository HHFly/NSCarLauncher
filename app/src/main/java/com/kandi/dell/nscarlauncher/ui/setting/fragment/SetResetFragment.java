package com.kandi.dell.nscarlauncher.ui.setting.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;

public class SetResetFragment extends BaseFragment {
    private BaseFragment mCurFragment;//当前页
    private static RelativeLayout fragmentShow;
    SetUserResetFragment setUserResetFragment;
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_reset;
    }

    @Override
    public void findView() {
        fragmentShow = getView(R.id.rl_set_reset);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        setClickListener(R.id.bt_resetcar);
    }

    @Override
    public void initView() {
        setUserResetFragment = new SetUserResetFragment();
    }

    /**
     * 选择Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(getActivity(), mCurFragment, fragment, R.id.set_reset);
        setVisibilityGone(R.id.rl_set_reset,true);
    }

    public static void hideFragment(){
        fragmentShow.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                UpgradeFragment.hideFragment();
                break;
            case R.id.bt_resetcar:
                switchFragment(setUserResetFragment);
                break;
        }
    }
}
