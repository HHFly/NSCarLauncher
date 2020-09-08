package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.CarModeAdapter;

public class EnergyCycleFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    private RelativeLayout car_choose_layout;
    private RecyclerView la_recyclerView;
    private TextView set_low,set_middle,set_high;
    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_car_portrait;
    }

    @Override
    public void findView() {
        car_choose_layout = getView(R.id.car_choose_layout);
        la_recyclerView = getView(R.id.la_recyclerView);
        set_low = getView(R.id.set_low);
        set_middle = getView(R.id.set_middle);
        set_high = getView(R.id.set_high);
    }

    @Override
    public void setListener() {
            setClickListener(R.id.bt_back);
            setClickListener(R.id.carset_layout);
            setClickListener(R.id.set_low);
            setClickListener(R.id.set_middle);
            setClickListener(R.id.set_high);
    }

    @Override
    public void initView() {
        CarModeAdapter carModeAdapter = new CarModeAdapter(homePagerActivity.getActivity().getApplicationContext());
        if (la_recyclerView != null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePagerActivity.getActivity().getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            la_recyclerView.setLayoutManager(linearLayoutManager);
            la_recyclerView.setAdapter(carModeAdapter);
        }
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_back:
                    homePagerActivity.getSetFragment().hideFragment();
                    break;
                case R.id.carset_layout:
                    car_choose_layout.setVisibility(car_choose_layout.getVisibility() == View.GONE?View.VISIBLE:View.GONE);
                    break;
                case R.id.set_low:
                    set_low.setSelected(true);
                    set_middle.setSelected(false);
                    set_high.setSelected(false);
                    //调用can服务发送数据
                    break;
                case R.id.set_middle:
                    set_low.setSelected(false);
                    set_middle.setSelected(true);
                    set_high.setSelected(false);
                    //调用can服务发送数据
                    break;
                case R.id.set_high:
                    set_low.setSelected(false);
                    set_middle.setSelected(false);
                    set_high.setSelected(true);
                    //调用can服务发送数据
                    break;
            }
    }
}
