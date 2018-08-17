package com.example.dell.nscarlauncher.ui.setting.adapter;

import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.example.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.example.dell.nscarlauncher.ui.setting.model.SetModel;

import java.util.List;

public class SetAdapter extends BaseListRvAdapter<SetModel> {
    public SetAdapter(List<SetModel> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_set;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos,final SetModel data) {
            holder.image(R.id.iv_set,data.getLogo());
            holder.text(R.id.tv_name,data.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onClickData(data);
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
        void onClickData(SetModel data);


    }
}
