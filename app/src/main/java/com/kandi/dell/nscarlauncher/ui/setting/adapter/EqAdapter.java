package com.kandi.dell.nscarlauncher.ui.setting.adapter;

import android.view.View;

import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui.fm.FMAdapter;
import com.kandi.dell.nscarlauncher.ui.setting.model.EqData;

import java.util.List;

public class EqAdapter extends BaseListRvAdapter<EqData> {
    public EqAdapter(List<EqData> data) {
        super(data);
    }
    public void DataClear(){
        for(EqData eqData:getData()){
            eqData.setSelect(false);
        }
    }
    @Override
    public int getItemResId() {
        return R.layout.item_eq_select;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos,final EqData data) {
        holder.text(R.id.tv_set_eq,data.getPreset());
        holder.get(R.id.tv_set_eq).setSelected(data.getSelect());
        holder.get(R.id.iv_set_eq).setSelected(data.getSelect());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(onItemClickListener!=null){
                    onItemClickListener.onClickMode(data);
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
        void onClickMode(EqData data);


    }
}
