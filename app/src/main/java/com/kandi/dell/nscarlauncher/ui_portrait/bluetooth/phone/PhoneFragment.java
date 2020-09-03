package com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.Activity.BaseActivity;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;

public class PhoneFragment  extends BaseFragment {
    public boolean flag_phone; //是否通话
    private BaseFragment mCurFragment;
    private PPhoneFragment pPhoneFragment ;//主界面
    private PMemberFragment pMemberFragment;
    private PRecordFragment pRecordFragment;
    @Override
    public int getContentResId() {
        return R.layout.fragment_phone_main;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
        setmType(FragmentType.PHONE);
        getpPhoneFragment();
        switchFragment(getpPhoneFragment());
    }

    @Override
    public void onClick(View v) {

    }
    public void switchPPFragment(){
        switchFragment(getpPhoneFragment());
    }
    public void switchPMFragment(){
        switchFragment(getpMemberFragment());
    }
    public void switchPRFragment(){
        switchFragment(getpRecordFragment());
    }
    public void switchFragment(BaseFragment fragment) {
        try{
            mCurFragment = FragmentUtils.selectFragment(getActivity(), mCurFragment, fragment, R.id.frame_phone_main);
//           mCurFragment.setmType(fragment.getmType());
            mCurFragment.Resume();
        }catch (Exception e){
        }
    }
    // 电话拨打
    public void phoneCall(String number) {
        switchFragment(getpPhoneFragment());
        getpPhoneFragment().phoneCall(number);
    }
    // 电话拨打
    public void callphone(String number) {
        switchFragment(getpPhoneFragment());
        getpPhoneFragment().callphone(number);
    }
    // 电话接通开始
    public void phoneStart() {
        switchFragment(getpPhoneFragment());
        getpPhoneFragment().phoneStart();
    }
    // 电话接通结束
    public void phoneStop(Context context) {
        Log.d("kondi", "BtPhone abandon audioFocus");

        getpPhoneFragment().phoneStop();

    }
    //
    public void callIn(String num,String addre,String ty){
        switchFragment(getpPhoneFragment());
        getpPhoneFragment().callIn(num,addre,ty);
    }
    public PMemberFragment getpMemberFragment() {
        if(pMemberFragment==null){
            pMemberFragment=new PMemberFragment();
        }
        return pMemberFragment;
    }

    public PRecordFragment getpRecordFragment() {
        if(pRecordFragment==null){
            pRecordFragment=new PRecordFragment();
        }
        return pRecordFragment;
    }

    public PPhoneFragment getpPhoneFragment() {
        if(pPhoneFragment==null){
            pPhoneFragment=new PPhoneFragment();
        }
        return pPhoneFragment;
    }
    public final int                    BOOKREFRESH   = 6;
    public final int                    RECORDREFRESH  =7;
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case BOOKREFRESH:
                        if(pMemberFragment!=null) {
                            pMemberFragment.refresh();
                        }
                        break;
                    case RECORDREFRESH:
                        if(pRecordFragment!=null) {
                            pRecordFragment.refresh();
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

}
