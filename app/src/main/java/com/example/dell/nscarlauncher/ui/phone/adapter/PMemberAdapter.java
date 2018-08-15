package com.example.dell.nscarlauncher.ui.phone.adapter;

import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.example.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.example.dell.nscarlauncher.ui.phone.model.MemberData;

import java.util.List;

public class PMemberAdapter extends BaseListRvAdapter<MemberData> {
    public PMemberAdapter(List<MemberData> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_phone_menber;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos,final MemberData data) {
        holder.text(R.id.name,data.getName());
        holder.text(R.id.number,data.getNumber());
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
        void onClickMem(MemberData data);


    }
}
