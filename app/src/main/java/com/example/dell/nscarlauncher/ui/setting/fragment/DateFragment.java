package com.example.dell.nscarlauncher.ui.setting.fragment;

import android.view.View;
import android.widget.CheckBox;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.example.dell.nscarlauncher.ui.setting.SetFragment;

public class DateFragment extends BaseFragment {
    private CheckBox checkBox ;
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_date;
    }

    @Override
    public void findView() {
        checkBox =getView(R.id.cb_date);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.rl_date);
        setClickListener(R.id.iv_return);
        setClickListener(R.id.cb_date);
    }

    @Override
    public void initView() {
        checkBox.setChecked(FlagProperty.isHourdate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_date:
                FlagProperty.isHourdate=! FlagProperty.isHourdate;
                checkBox.setChecked(FlagProperty.isHourdate);
                break;
            case R.id.iv_return:
                SetFragment.hideFragment();
                break;
            case R.id.cb_date:
                FlagProperty.isHourdate=! FlagProperty.isHourdate;
                checkBox.setChecked(FlagProperty.isHourdate);
                break;
        }
    }
}
