package com.example.dell.nscarlauncher.ui.home;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.home.adapter.HomeAdapter;
import com.example.dell.nscarlauncher.ui.home.model.HomeModel;

import java.util.ArrayList;

import static android.support.v7.widget.LinearLayoutManager.*;

public class HomePagerOneFragment extends BaseFragment {
    private HomeAdapter mAdapter;
    private ArrayList<HomeModel> mData = new ArrayList<>();


    public void setmData(ArrayList<HomeModel> Data) {
       for (int i =0 ;i<Data.size();i++){
           HomeModel homeModel =new HomeModel(Data.get(i));
           mData.add(homeModel);
       }
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_home;
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

    @Override
    public void onClick(View view) {

    }

    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( ArrayList<HomeModel> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView);
            mAdapter =new HomeAdapter(mData);

            if (rv != null) {
                LinearLayoutManager ms= new LinearLayoutManager(getContext());
                ms.setOrientation(HORIZONTAL);
                rv.setLayoutManager(ms);
                rv.setAdapter(mAdapter);
            }
//            mAdapter.setOnItemClickListener(new MeAdapter.OnItemClickListener() {
//                @Override
//                public void onClickUserInfo() {
//                    ActivityJumpUtils.actUserInfo(getActivity());
//                }
//
//                @Override
//                public void onClickUserPic(int id) {
//                    FunctionDialogFactory.showTakePhoneIDDialog(meFragment, id);
//                }
//
//
//            });

        }
    }
}
