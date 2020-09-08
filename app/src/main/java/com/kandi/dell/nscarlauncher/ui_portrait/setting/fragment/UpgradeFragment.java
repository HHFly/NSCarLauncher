package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.widget.AddOneEtParamDialog;

public class UpgradeFragment extends BaseFragment {
    private BaseFragment mCurFragment;//当前页
    private RelativeLayout fragmentShow;
    SetResetFragment setResetFragment;
    private HomePagerActivity homePagerActivity;

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_set_upgrade_portrait;
    }

    @Override
    public void findView() {
        fragmentShow = getView(R.id.rl_set_upgrade);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
        setClickListener(R.id.sys_upgrade);
        setClickListener(R.id.factory_reset);
    }

    @Override
    public void initView() {
        setResetFragment =new SetResetFragment();
    }

    /**
     * 选择Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(getActivity(), mCurFragment, fragment, R.id.set_upgrade);
        setVisibilityGone(R.id.rl_set_upgrade,true);
    }

    public void hideFragment(){
        fragmentShow.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_back:
                homePagerActivity.getSetFragment().hideFragment();
                break;
            case R.id.sys_upgrade:
//                switchFragment(setResetFragment);
                ShowDialog();
                break;
            case R.id.factory_reset:
                switchFragment(getSetResetFragment());
                break;
        }
    }

    //    填写信息dialog
    private  void  ShowDialog(){
        AddOneEtParamDialog mAddOneEtParamDialog = AddOneEtParamDialog.getInstance(1);

        mAddOneEtParamDialog.setOnDialogClickListener(new AddOneEtParamDialog.DefOnDialogClickListener() {
            @Override
            public void onClickCommit(AddOneEtParamDialog dialog, String data) {
                App.get().getCurActivity().initImmersionBar();
                dialog.dismiss();
            }

            @Override
            public void onClickCancel(AddOneEtParamDialog dialog) {
                App.get().getCurActivity().initImmersionBar();
                dialog.dismiss();
            }
        });

        mAddOneEtParamDialog.show(this.getFragmentManager());
    }

    public SetResetFragment getSetResetFragment() {
        if(setResetFragment == null){
            setResetFragment = new SetResetFragment();
            setResetFragment.setHomePagerActivity(homePagerActivity);
        }
        return setResetFragment;
    }
}
