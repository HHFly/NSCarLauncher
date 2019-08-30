package com.kandi.dell.nscarlauncher.ui.tachograph.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kandi.dell.nscarlauncher.R;

public class DialogPhoto {
    Dialog dialog;
    Window window;
    Context context;
    //显示图片的控件
    private ImageView ivPhoto;
    //返回上层界面的控件
    private ImageButton ibFinish;
    public  boolean isShow;
    public DialogPhoto(Context context) {
        dialog = new Dialog(context, R.style.nodarken_style);
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        window = dialog.getWindow();
        window.setContentView(R.layout.activity_photo);
        this.context =context;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity= Gravity.TOP;
        lp.y=60;
        window.setAttributes(lp);
        ibFinish = (ImageButton) window.findViewById(R.id.ib_finish);
        ibFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isShow=false;
            }
        });
        ivPhoto = (ImageView) window.findViewById(R.id.iv_photo);
    }

    public void cancel(){
        dialog.cancel();
        isShow=false;
    }
    public  void  show(String url){
        //图片加载
        Glide.with(context).load(url).into(ivPhoto);
        dialog.show();
        isShow=true;
    }
}
