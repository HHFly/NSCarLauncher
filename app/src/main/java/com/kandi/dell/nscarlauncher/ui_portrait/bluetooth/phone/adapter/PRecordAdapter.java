package com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.adapter;

import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneRecordInfo;

import java.util.List;

public class PRecordAdapter extends BaseListRvAdapter<PhoneRecordInfo> {
    public PRecordAdapter(List<PhoneRecordInfo> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_phone_record;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos,final PhoneRecordInfo data) {
        holder.text(R.id.name,data.getName());
        holder.text(R.id.number,data.getNumber());
        holder.text(R.id.call_count,data.getCall_count());
        holder.text(R.id.call_time,data.getCall_time());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onClickMem(data);
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
        void onClickMem(PhoneRecordInfo data);


    }
}
