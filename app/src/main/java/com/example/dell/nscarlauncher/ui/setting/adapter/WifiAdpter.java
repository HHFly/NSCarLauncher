package com.example.dell.nscarlauncher.ui.setting.adapter;

import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.example.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.example.dell.nscarlauncher.ui.setting.model.Wifiinfo;

import java.util.List;

public class WifiAdpter extends BaseListRvAdapter<Wifiinfo>{
    public WifiAdpter(List<Wifiinfo> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_wifi_set;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos, final Wifiinfo data) {
        holder.text(R.id.wifi_info_name,data.name);
        holder.text(R.id.wifi_info_state,data.state);
        holder.image(R.id.wif_info_intensity,data.signal_intensity);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(data);
                }
            }
        });
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
    void onClick(Wifiinfo data);


}
}
