package com.kandi.dell.nscarlauncher.ui.fm;

import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;

import java.util.List;

public class FMAdapter extends BaseListRvAdapter<Float> {
    public FMAdapter(List<Float> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_fm_fa;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos, final  Float data) {
        holder.text(R.id.tv_fm_re_hz,data.toString());
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
        void onClickFM(Float data);


    }
}
