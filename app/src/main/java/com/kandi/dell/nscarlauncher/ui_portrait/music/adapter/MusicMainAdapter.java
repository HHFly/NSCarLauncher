package com.kandi.dell.nscarlauncher.ui_portrait.music.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.kandi.dell.nscarlauncher.base.adapter.BaseRvAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.music.model.Mp3Info;

import java.util.List;

public class MusicMainAdapter extends BaseListRvAdapter<Mp3Info> {


    public MusicMainAdapter(List<Mp3Info> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_music_main_list;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, final int bodyPos,final Mp3Info data) {
        holder.text(R.id.item_songname,data.displayName);
        holder.text(R.id.item_songtime, "-"+data.artist);
        boolean isSelected = App.get().getCurActivity().getDialogLocalMusicD().musicDiverID == bodyPos;
        holder.get(R.id.item_songname).setSelected(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID == bodyPos);
        holder.visibility(R.id.iv_music_selected,isSelected);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onItemClickListener!=null){
                    onItemClickListener.onClickMusic(data,bodyPos);
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
        void onClickMusic(Mp3Info data, int Pos);


    }
}
