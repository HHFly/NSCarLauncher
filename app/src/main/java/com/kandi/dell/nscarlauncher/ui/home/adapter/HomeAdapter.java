package com.kandi.dell.nscarlauncher.ui.home.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.MultipleSourcesRvAdapter;
import com.kandi.dell.nscarlauncher.ui.home.model.HomeModel;


import java.util.ArrayList;

public class HomeAdapter extends MultipleSourcesRvAdapter {
    private ArrayList<HomeModel> mData;
    public HomeAdapter(ArrayList<HomeModel> data) {
        this.mData=data;
    }

    @Override
    public int getRowsCountInSection(int var1) {
        return 1;
    }
    @Override
    public int getSectionsCount() {
        return mData.size();
    }
    @Override
    public View onCreateView(ViewGroup var1, int var2) {
        return inflater(var1, mData.get(var2).getLayout());
    }

    @Override
    public void onBindViewHolder(AutoViewHolder holder, IndexPath indexPath) {

    }
}
