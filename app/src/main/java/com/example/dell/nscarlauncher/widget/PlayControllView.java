package com.example.dell.nscarlauncher.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.dell.nscarlauncher.R;

public class PlayControllView extends LinearLayout {
    private final String TAG = "PlayControllView";

    private ImageView left;
    private ImageView center;
    private ImageView right;

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

    }
    public void setRLvisiable(boolean isShow){
        left.setVisibility(isShow?View.VISIBLE : View.GONE);
        right.setVisibility(isShow?View.VISIBLE : View.GONE);
    }
    public void setPlay(boolean isPlay){
        center.setImageResource(isPlay?R.mipmap.ic_pause:R.mipmap.ic_play);
    }
    public void setCenterOnClickListener(OnClickListener clickListener) {
        if(center != null)
            center.setOnClickListener(clickListener);
    }
    public void setLeftOnClickListener(OnClickListener clickListener) {
        if(left != null)
            left.setOnClickListener(clickListener);
    }
    public void setRightOnClickListener(OnClickListener clickListener) {
        if(right != null)
            right.setOnClickListener(clickListener);
    }
}
