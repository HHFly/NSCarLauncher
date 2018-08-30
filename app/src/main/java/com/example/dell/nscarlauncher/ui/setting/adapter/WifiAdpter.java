package com.example.dell.nscarlauncher.ui.setting.adapter;

import com.example.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.example.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.example.dell.nscarlauncher.ui.setting.model.WifiInfo;

import java.util.List;

public class WifiAdpter extends BaseListRvAdapter<WifiInfo>{
    public WifiAdpter(List<WifiInfo> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return 0;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos, WifiInfo data) {

    }


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

public interface OnItemClickListener {
    /**
     * 点击
     *
     *
     */
    void onClickMusic(WifiInfo data,int Pos);


}
}
