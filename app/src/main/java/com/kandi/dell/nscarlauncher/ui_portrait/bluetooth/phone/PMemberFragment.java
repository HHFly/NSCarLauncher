package com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;

import com.kandi.dell.nscarlauncher.ui.phone.model.NameBean;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneBookInfo;
import com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.adapter.PMemberAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.adapter.SectionDecoration;
import com.kandi.dell.nscarlauncher.widget.HintSideBar;
import com.kandi.dell.nscarlauncher.widget.SideBar;

import java.util.ArrayList;
import java.util.List;

public class PMemberFragment extends BaseFragment implements SideBar.OnChooseLetterChangedListener{
    private PMemberAdapter mAdapter;
    private List<PhoneBookInfo> mData =new ArrayList<>();
    HintSideBar hintSideBar;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv ;
    @Override
    public int getContentResId() {
        return R.layout.fragment_phone_member_por;
    }

    @Override
    public void findView() {
        hintSideBar = (HintSideBar) getView(R.id.hintSideBar);
    }

    @Override
    public void setListener() {
            hintSideBar.setOnChooseLetterChangedListener(this);
    }

    @Override
    public void initView() {
        mData = App.get().getCurActivity().getPhoneInfoService().getPhoneBookInfos();
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

    @Override
    public void onChooseLetter(String s) {
        int i = mAdapter.getFirstPositionByChar(s);
        if (i == -1) {
            return;
        }
        linearLayoutManager.scrollToPositionWithOffset(i, 0);
    }

    @Override
    public void onNoChooseLetter() {

    }
    public void  refresh(){
        initRvAdapter(App.get().getCurActivity().getPhoneInfoService().getPhoneBookInfos());
    }
    /**
     * 初始化adapter
     *
     *
     */
    private List<NameBean > dataList;
    public void initRvAdapter( List<PhoneBookInfo> data) {
        if(data != null && data.size()>0){
            hintSideBar.setVisibility(View.VISIBLE);

        }else{
            hintSideBar.setVisibility(View.GONE);

        }
        if (mAdapter == null) {
            rv = getView(R.id.recyclerView2);
            mAdapter =new PMemberAdapter(data);

            if (rv != null) {
                setPullAction(mData);
                rv.addItemDecoration(new SectionDecoration(dataList,getContext(), new SectionDecoration.DecorationCallback() {
                    //返回标记id (即每一项对应的标志性的字符串)
                    @Override
                    public String getGroupId(int position) {
                        if(dataList.get(position).getName()!=null) {
                            return dataList.get(position).getName();
                        }
                        return "-1";
                    }

                    //获取同组中的第一个内容
                    @Override
                    public String getGroupFirstLine(int position) {
                        if(dataList.get(position).getName()!=null) {
                            return dataList.get(position).getName();
                        }
                        return "";
                    }
                }));
                linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new PMemberAdapter.OnItemClickListener() {

                @Override
                public void onClickMem(PhoneBookInfo data) {

                    App.get().getCurActivity().getPhoneFragment().callphone(data.getNumber());
                }


            });

        }else {
            if(rv==null){
                setPullAction(mData);
                rv.addItemDecoration(new SectionDecoration(dataList,getContext(), new SectionDecoration.DecorationCallback() {
                    //返回标记id (即每一项对应的标志性的字符串)
                    @Override
                    public String getGroupId(int position) {
                        if(dataList.get(position).getName()!=null) {
                            return dataList.get(position).getName();
                        }
                        return "-1";
                    }

                    //获取同组中的第一个内容
                    @Override
                    public String getGroupFirstLine(int position) {
                        if(dataList.get(position).getName()!=null) {
                            return dataList.get(position).getName();
                        }
                        return "";
                    }
                }));
                rv = getView(R.id.recyclerView2);
                if (rv != null) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    rv.setLayoutManager(linearLayoutManager);
                    rv.setAdapter(mAdapter);
                }
            }
            mAdapter.notifyData(data,true);
        }

    }
    private void setPullAction( List<PhoneBookInfo> comingslist) {
         dataList = new ArrayList<>();

        for (int i = 0; i < comingslist.size(); i++) {
            NameBean nameBean = new NameBean();
            String name0 = comingslist.get(i).getFirstLetter();
            nameBean.setName(name0);
            dataList.add(nameBean);
        }
    }


}
