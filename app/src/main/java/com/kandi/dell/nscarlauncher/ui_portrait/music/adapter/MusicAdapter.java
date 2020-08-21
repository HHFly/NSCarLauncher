package com.kandi.dell.nscarlauncher.ui_portrait.music.adapter;

import android.view.View;
import android.widget.CheckBox;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.music.model.Mp3Info;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MusicAdapter extends BaseListRvAdapter<Mp3Info> {
    private int mode=1;//1 local 2 usb 3  sd
    public boolean isShow = false;
    public void setMode(int mode) {
        this.mode = mode;
    }
    public MusicAdapter(List<Mp3Info> data) {
        super(data);
    }
    public Map<Integer, Mp3Info> recodeStatu = new HashMap<>();
    @Override
    public int getItemResId() {
        return R.layout.item_music_list;
    }

    public void setRecodeStatu(Map<Integer, Mp3Info> recodeStatu) {
        this.recodeStatu = recodeStatu;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, final int bodyPos, final Mp3Info data) {
        holder.text(R.id.item_songname,data.displayName);
        holder.text(R.id.item_songtime, data.artist);

        boolean isSelected =App.get().getCurActivity().getDialogLocalMusicD().musicDiverID == bodyPos;
        holder.get(R.id.item_songname).setSelected(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID == bodyPos);
        holder.visibility(R.id.iv_music_selected,isSelected);
        final CheckBox checkBox = holder.get(R.id.checkBox);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isShow){
                    checkBox.performClick();
                    return;
                }
                if(onItemClickListener!=null&&!isShow){
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

            checkBox.setChecked(data.isCheck());
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked()){
                        recodeStatu.put(bodyPos,data);

                    }else {
                        recodeStatu.remove(bodyPos);
                    }
                    data.setCheck(checkBox.isChecked());
                }
            });
            holder.visibility(R.id.iv_music_list_delete,!isShow);
            holder.visibility(R.id.checkBox,isShow);



    }

    // 时间格式化为00
    public static String getTime(int time) {

        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
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

        void  onLongClickMusic(Mp3Info data, int Pos);

        void onDelete(Mp3Info data, int Pos);
    }
}
