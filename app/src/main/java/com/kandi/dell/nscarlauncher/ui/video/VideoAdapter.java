package com.kandi.dell.nscarlauncher.ui.video;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.kandi.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;
import com.kandi.dell.nscarlauncher.widget.SwipeMenuLayout;

import java.util.List;

import static com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity.homePagerActivity;

public class VideoAdapter  extends BaseListRvAdapter<Mp3Info> {
    public VideoAdapter(List<Mp3Info> data) {
        super(data);
    }

    private int mode=1;//1 local 2 usb 3 collect
    public boolean isShow = false;

    public void setMode(int mode) {
        this.mode = mode;
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


                if (onItemClickListener != null && !SwipeMenuLayout.isUserSwiped) {
                    onItemClickListener.onClickMusic(v, data, bodyPos);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onLongClickMusic(data,bodyPos);
                }
                return true;
            }


        });
        if(isShow){
            CheckBox checkBox = holder.get(R.id.checkBox);
            checkBox.setVisibility(View.VISIBLE);
            Boolean flag = homePagerActivity.getVideoFragment().recodeStatu.get(bodyPos);
            if (flag == null) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(flag);
            }
        }else{
            holder.get(R.id.checkBox).setVisibility(View.INVISIBLE);
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        /**
         * 点击
         */
        void onClickMusic(View view,Mp3Info data, int Pos);

        void  onLongClickMusic(Mp3Info data,int Pos);
    }
}
