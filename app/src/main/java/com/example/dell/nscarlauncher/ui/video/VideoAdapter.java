package com.example.dell.nscarlauncher.ui.video;

import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.example.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.example.dell.nscarlauncher.ui.music.model.Mp3Info;

import java.util.List;

public class VideoAdapter  extends BaseListRvAdapter<Mp3Info> {
    public VideoAdapter(List<Mp3Info> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_musiclist;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, final int bodyPos, final Mp3Info data) {
        holder.text(R.id.item_songname, data.displayName);
        holder.text(R.id.item_singer, data.artist);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (onItemClickListener != null) {
                    onItemClickListener.onClickMusic(data, bodyPos);
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
         */
        void onClickMusic(Mp3Info data, int Pos);


    }
}
