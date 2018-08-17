package com.example.dell.nscarlauncher.ui.setting;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.phone.PhoneFragment;
import com.example.dell.nscarlauncher.ui.phone.adapter.PMemberAdapter;
import com.example.dell.nscarlauncher.ui.phone.model.PhoneBookInfo;
import com.example.dell.nscarlauncher.ui.setting.adapter.SetAdapter;
import com.example.dell.nscarlauncher.ui.setting.eumn.SetType;
import com.example.dell.nscarlauncher.ui.setting.model.SetData;
import com.example.dell.nscarlauncher.ui.setting.model.SetModel;

import java.util.List;

public class SetFragment extends BaseFragment {
    private SetData  mData =new SetData();
    private SetAdapter mAdapter;

    @Override
    public int getContentResId() {
        return R.layout.fragment_set;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
      initRvAdapter(mData.getData());
    }

    @Override
    public void onClick(View v) {

    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<SetModel> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView);
            mAdapter =new SetAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new SetAdapter.OnItemClickListener() {

                @Override
                public void onClickData(SetModel data) {
                    Click(data);
                }

            });

        }else {
            mAdapter.notifyDataSetChanged();
        }

    }
    private void Click(SetModel data){
            switch (data.getItem()){
                case SetType.DISPLAY:
                    break;
            }
    }
}
