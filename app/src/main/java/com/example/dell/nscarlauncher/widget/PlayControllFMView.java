package com.example.dell.nscarlauncher.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.dell.nscarlauncher.R;

public class PlayControllFMView extends LinearLayout {
    private final String TAG = "PlayControllFMView";

    private ImageView left;
    private ImageView center;
    private ImageView right;
    public boolean isPlay;

    public PlayControllFMView(Context context) {
        super(context);
    }

    public PlayControllFMView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_controll_fm, this);
        left=(ImageView) findViewById(R.id.ctl_iv_left);
        center=(ImageView) findViewById(R.id.ctl_iv_center);
        right=(ImageView) findViewById(R.id.ctl_iv_right);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onClickLeft();
                }
            }
        });
        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlay=!isPlay;
                if(onItemClickListener!=null){
                    onItemClickListener.onClickCenter(isPlay);
                }
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
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
        center.setImageResource(isPlay?R.mipmap.ic_play:R.mipmap.ic_off);
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
