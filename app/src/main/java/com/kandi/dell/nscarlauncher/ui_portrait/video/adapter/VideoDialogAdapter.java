package com.kandi.dell.nscarlauncher.ui_portrait.video.adapter;

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

public class VideoDialogAdapter  extends BaseListRvAdapter<Mp3Info> {
    private int mode=1;//1 local 2 usb 3  sd
    public boolean isShow = false;
    public void setMode(int mode) {
        this.mode = mode;
    }

    public Map<Integer, Mp3Info> recodeStatu = new HashMap<>();
    public void setRecodeStatu(Map<Integer, Mp3Info> recodeStatu) {
        this.recodeStatu = recodeStatu;
    }

    public VideoDialogAdapter(List<Mp3Info> data) {
        super(data);
    }

    @Override
    public int getItemResId() {
        return R.layout.item_video_list;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos, Mp3Info data) {
        holder.text(R.id.item_songname,data.displayName);


        boolean isSelected = App.get().getCurActivity().getDialogVideo().musicDiverID == bodyPos;
        holder.get(R.id.item_songname).setSelected(isSelected);
        holder.visibility(R.id.iv_music_selected,isSelected);
        final CheckBox checkBox = holder.get(R.id.checkBox);
        holder.get(R.id.iv_music_list_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null&&!isShow){
                    onItemClickListener.onDelete(data,bodyPos);
                }
            }
        });
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
