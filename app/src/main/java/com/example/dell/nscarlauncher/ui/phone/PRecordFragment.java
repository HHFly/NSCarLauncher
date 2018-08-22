package com.example.dell.nscarlauncher.ui.phone;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.phone.adapter.PMemberAdapter;
import com.example.dell.nscarlauncher.ui.phone.adapter.PRecordAdapter;
import com.example.dell.nscarlauncher.ui.phone.model.PhoneBookInfo;
import com.example.dell.nscarlauncher.ui.phone.model.PhoneRecordInfo;

import java.util.ArrayList;
import java.util.List;

public class PRecordFragment extends BaseFragment {
    private PRecordAdapter mAdapter;
    private List<PhoneRecordInfo> mData =new ArrayList<>();

    public void setmData(List<PhoneRecordInfo> mData) {
        this.mData = mData;
        initRvAdapter(this.mData);
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_phone_member;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void onClick(View v) {

    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<PhoneRecordInfo> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView1);
            mAdapter =new PRecordAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new PRecordAdapter.OnItemClickListener() {

                @Override
                public void onClickMem(PhoneRecordInfo data) {
                    PhoneFragment.callphone(data.getNumber());
                }



            });

        }else {
            mAdapter.notifyDataSetChanged();
        }

    }
}
