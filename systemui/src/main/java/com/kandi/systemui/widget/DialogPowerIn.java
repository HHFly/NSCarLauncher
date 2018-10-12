package com.kandi.systemui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kandi.systemui.R;

public class DialogPowerIn {
    Context content ;
    Dialog alertDialog;
    WaveView waveView;
    ImageView iv_power_logo;
    TextView  tv_power_level, tv_tip;
    public boolean isShow =false;
    public boolean isError =false;
    public DialogPowerIn(Context content) {
        this.content = content;
        incomingShow();
    }

    public void setContent(Context content){
        this.content = content;

    }
    public void setProgress(int progress){

        waveView.setProgress(progress);
        tv_power_level.setText(progress+"%");
        if(!isError) {
            tv_tip.setText(content.getResources().getString(R.string.正在充电中));
            if (100 == progress) {
                tv_tip.setText(content.getResources().getString(R.string.充电已完成));
            }
        }

    }
    public void setPowerError(boolean isErro){
        isError =isErro;
        if(isError){
            tv_tip.setText(content.getResources().getString(R.string.充电异常));
            tv_tip.setTextColor(Color.parseColor("#DE1419"));

            iv_power_logo.setSelected(true);
        }else {
            tv_tip.setText(content.getResources().getString(R.string.正在充电中));
            tv_tip.setTextColor(Color.parseColor("#EEEEEE"));
            iv_power_logo.setSelected(false);

        }
    }
    // 显示弹出框
    public void incomingShow() {

        alertDialog = new Dialog(content);
        alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog消失
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                waveView.setPause(true);
            }
        });
        Window window = alertDialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setContentView(R.layout.dialog_powerin);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        waveView=window.getDecorView().findViewById(R.id.wave_view);
        tv_power_level=window.getDecorView().findViewById(R.id.tv_power_level);
        tv_tip=window.getDecorView().findViewById(R.id.tv_tip);
        iv_power_logo =window.getDecorView().findViewById(R.id.iv_power_logo);
// 设置具体参数
        WindowManager.LayoutParams lp = window.getAttributes();
//		lp.x = 0;
//		lp.y = -400;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

    }
    public void  show(){
        isShow =true;
        alertDialog.show();
        waveView.setPause(false);

    }
        public void dissmiss(){
        alertDialog.dismiss();
            waveView.setPause(true);
        isShow =false;
        }
}
