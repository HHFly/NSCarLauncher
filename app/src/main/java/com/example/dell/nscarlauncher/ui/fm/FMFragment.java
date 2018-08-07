package com.example.dell.nscarlauncher.ui.fm;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.HomePagerActivity;
import com.example.dell.nscarlauncher.ui.home.adapter.HomeAdapter;
import com.example.dell.nscarlauncher.ui.home.model.HomeModel;
import com.example.dell.nscarlauncher.widget.RadioRulerView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

public class FMFragment extends BaseFragment implements RadioRulerView.OnValueChangeListener{
    private FMAdapter mAdapter ;
    private RadioRulerView mRule;
    public static ArrayList<String> fm_list = new ArrayList<String>();
    private HomePagerActivity homePagerActivity;

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_fm;
    }

    @Override
    public void findView() {
        mRule=getView(R.id.rule);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_back);
        setClickListener(R.id.iv_search);
        mRule.setOnValueChangeListener(this);
    }

    @Override
    public void initView() {
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                homePagerActivity.hideFragment();
                break;
            case R.id.iv_search:
                mRule.startAutoSeachFM();
                break;
        }
    }
    private void  initData(){
        initFMList();
        initRvAdapter(fm_list);
    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( ArrayList<String> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView);
            mAdapter =new FMAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new FMAdapter.OnItemClickListener() {
                @Override
                public void onClickFM(String data) {

                }

            });

        }else {
            mAdapter.notifyDataSetChanged();
        }
    }
    // 初始化一些音乐频道
    public void initFMList() {
        fm_list.add("89.0");
        fm_list.add("93.0");
        fm_list.add("95.0");
        fm_list.add("96.8");
        fm_list.add("98.8");
        fm_list.add("103.2");
    }

    @Override
    public void onValueChange(float value) {
        setTvText(R.id.tv_fm_Hz,String.valueOf(value));
    }
}
