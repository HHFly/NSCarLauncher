package com.kandi.dell.nscarlauncher.ui.phone;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.phone.adapter.PRecordAdapter;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneRecordInfo;

import java.util.ArrayList;
import java.util.List;

public class PRecordFragment extends BaseFragment {
    private PRecordAdapter mAdapter;
    private List<PhoneRecordInfo> mData =new ArrayList<>();
    RecyclerView rv ;
    public void setmData(List<PhoneRecordInfo> mData) {
        this.mData = mData;
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_phone_record;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
        initRvAdapter(mData);
    }
    public void  refresh(){
        initRvAdapter(mData);
    }
    @Override
    public void onClick(View v) {

    }
    /**
     * 初始化adapter
     *
     *
     */
    public void initRvAdapter( List<PhoneRecordInfo> data) {

        if (mAdapter == null) {
            rv = getView(R.id.recyclerView1);
            mAdapter =new PRecordAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new PRecordAdapter.OnItemClickListener() {

                @Override
                public void onClickMem(PhoneRecordInfo data) {
                    HomePagerActivity.homePagerActivity.getPhoneFragment().callphone(data.getNumber());
                }



            });

        }else {
            if(rv==null){
                rv = getView(R.id.recyclerView1);
                if (rv != null) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    rv.setLayoutManager(linearLayoutManager);
                    rv.setAdapter(mAdapter);
                }
            }
            mAdapter.notifyData(data,true);
        }

    }

}
