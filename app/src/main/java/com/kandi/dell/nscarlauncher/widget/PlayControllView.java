package com.kandi.dell.nscarlauncher.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;

public class PlayControllView extends LinearLayout {
    private final String TAG = "PlayControllView";

    private ImageView left;
    public ImageView center;
    private ImageView right;
    public boolean isPlay;
    private TextView ctl_tv_center;
    public PlayControllView(Context context) {
        super(context);
    }

    public PlayControllView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_controll_1, this);
        left=(ImageView) findViewById(R.id.ctl_iv_left);
        center=(ImageView) findViewById(R.id.ctl_iv_center);
        right=(ImageView) findViewById(R.id.ctl_iv_right);
        ctl_tv_center=  findViewById(R.id.ctl_tv_center);
        left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onClickLeft();
                }
            }
        });
        center.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlay=!isPlay;
                if(onItemClickListener!=null){
                    onItemClickListener.onClickCenter(isPlay);
                }
            }
        });
        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onClickRight();
                }
            }
        });
    }
    public void setRLvisiable(boolean isShow){
        left.setVisibility(isShow?View.VISIBLE : View.GONE);
        right.setVisibility(isShow?View.VISIBLE : View.GONE);
    }
    public void setPlay(boolean isPlay){
        center.setImageResource(isPlay?R.mipmap.ic_pause:R.mipmap.ic_music_home_stop);
        ctl_tv_center.setText(isPlay?getContext().getString(R.string.播放):getContext().getString(R.string.暂停));
        this.isPlay =isPlay;
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
        void onClickLeft();
        void onClickCenter(boolean isPlay);
        void onClickRight();
    }

}
