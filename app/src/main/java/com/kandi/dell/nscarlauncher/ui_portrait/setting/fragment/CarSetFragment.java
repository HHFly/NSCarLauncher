package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.os.SystemProperties;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.internal.widget.LinearLayoutManager;
import com.android.internal.widget.RecyclerView;
import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.CarModeAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.LanguageAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class CarSetFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    private RelativeLayout car_choose_layout;
    private RecyclerView la_recyclerView;
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
    }

    @Override
    public void setListener() {
            setClickListener(R.id.bt_back);
            setClickListener(R.id.carset_layout);
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
            }
    }
}
