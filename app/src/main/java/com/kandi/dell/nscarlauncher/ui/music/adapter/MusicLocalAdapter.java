package com.kandi.dell.nscarlauncher.ui.music.adapter;

import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;

import java.util.List;

import static com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity.homePagerActivity;

public class MusicLocalAdapter extends BaseListRvAdapter<Mp3Info> {
    public MusicLocalAdapter(List<Mp3Info> data) {
        super(data);
    }
    private int mode=1;//1 local 2 usb 3 collect

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int getItemResId() {
        return R.layout.item_music_local;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, final int bodyPos,final Mp3Info data) {
        holder.text(R.id.item_songname,data.displayName);
        holder.text(R.id.item_singer,data.artist);
        holder.itemView.setSelected( homePagerActivity.getDialogLocalMusic().musicID==bodyPos);
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
        holder.get(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClickDelete(data,bodyPos);
                }
            }
        });
        holder.get(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClickCopy(data,bodyPos);
                }
            }
        });
        holder.get(R.id.btn_delete).setVisibility(1==mode?View.VISIBLE:View.GONE);
        holder.get(R.id.btn_copy).setVisibility(2==mode?View.VISIBLE:View.GONE);
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

        void  onClickDelete(Mp3Info data,int Pos);

        void  onClickCopy(Mp3Info data,int Pos);
    }
}
