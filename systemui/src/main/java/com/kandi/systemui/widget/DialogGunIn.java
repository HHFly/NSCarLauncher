package com.kandi.systemui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.kandi.systemui.R;

public class DialogGunIn {
    Context content ;
    Dialog alertDialog;
    public void setContent(Context content){
        this.content = content;

    }
    // 显示弹出框
    public void incomingShow() {

        alertDialog = new Dialog(content);
        alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog消失
        Window window = alertDialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setContentView(R.layout.dialog_gunin);

// 设置具体参数
        WindowManager.LayoutParams lp = window.getAttributes();
//		lp.x = 0;
//		lp.y = -400;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

    }
    public void  show(){

        alertDialog.show();
    }
        public void dissmiss(){
        alertDialog.dismiss();
        }
}
