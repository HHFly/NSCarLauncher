package com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneRecordInfo;
import com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.adapter.PRecordAdapter;

import java.util.ArrayList;
import java.util.List;

public class PRecordFragment extends BaseFragment {
    private PRecordAdapter mAdapter;
    private List<PhoneRecordInfo> mData =new ArrayList<>();
    RecyclerView rv ;
    @Override
    public int getContentResId() {
        return R.layout.fragment_phone_record_por;
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
        mData =App.get().getCurActivity().getPhoneInfoService().getPhoneRecordInfos();
        initRvAdapter(mData);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                App.get().getCurActivity().getPhoneFragment().switchPPFragment();
                break;
        }
    }

    public void setmData(List<PhoneRecordInfo> mData) {
        this.mData = mData;
    }
    public void  refresh(){
        initRvAdapter(mData);
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
                    App.get().getCurActivity().getPhoneFragment().callphone(data.getNumber());
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
