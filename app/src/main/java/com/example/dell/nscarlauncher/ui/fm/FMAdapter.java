package com.example.dell.nscarlauncher.ui.fm;

import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.example.dell.nscarlauncher.base.adapter.BaseListRvAdapter;

import java.util.List;

public class FMAdapter extends BaseListRvAdapter<String> {
    public FMAdapter(List<String> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_fm_fa;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos, final  String data) {
        holder.text(R.id.tv_fm_re_hz,data);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(onItemClickListener!=null){
                    onItemClickListener.onClickFM(data);
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
        void onClickFM(String data);


    }
}
