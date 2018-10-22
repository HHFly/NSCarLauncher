package com.kandi.dell.nscarlauncher.ui.music.adapter;

import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;

import java.util.List;

public class MusicLocalAdapter extends BaseListRvAdapter<Mp3Info> {
    public MusicLocalAdapter(List<Mp3Info> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_music_local;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, final int bodyPos,final Mp3Info data) {
        holder.text(R.id.item_songname,data.displayName);
        holder.text(R.id.item_singer,data.artist);
        holder.itemView.setSelected(DialogLocalMusic.musicID==bodyPos);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(onItemClickListener!=null){
                    onItemClickListener.onClickMusic(data,bodyPos);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onLongClickMusic(data,bodyPos);
                }
                return false;
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
        void onClickMusic(Mp3Info data,int Pos);

        void  onLongClickMusic(Mp3Info data,int Pos);
    }
}
