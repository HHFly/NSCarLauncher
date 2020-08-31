package com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.adapter;

import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneBookInfo;

import java.util.List;

public class PMemberAdapter extends BaseListRvAdapter<PhoneBookInfo> {
    public PMemberAdapter(List<PhoneBookInfo> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_phone_member_por;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos,final PhoneBookInfo data) {
        holder.text(R.id.name,data.getName());
//        holder.text(R.id.number,data.getNumber());
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
        void onClickMem(PhoneBookInfo data);


    }
    public int getFirstPositionByChar(String sign) {
        if (sign.charAt(0) == '↑' || sign.charAt(0) == '☆') {
            return 0;
        }
        if(sign.equals("Z")){//由于识别中文不准确，规避方式，但仍有bug
            for (int i = getData().size()-1; i > 0; i--) {
                if(!getData().get(i).getFirstLetter().equals(sign.toLowerCase())){
                    return i == getData().size() -1?i:i+1;
                }
            }
        }else{
            for (int i = 0; i < getData().size(); i++) {
                if (getData().get(i).getFirstLetter().equals(sign.toLowerCase())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
