package com.kandi.dell.nscarlauncher.ui.tachograph.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hikvision.playerlibrary.HikLog;
import com.hikvision.playerlibrary.HikRecordPlayer;
import com.hikvision.playerlibrary.HikVideoCallBack;
import com.hikvision.playerlibrary.HikVideoConstant;
import com.hikvision.playerlibrary.HikVideoModel;
import com.kandi.dell.nscarlauncher.R;

import java.text.DecimalFormat;

public class DialogReview implements HikVideoCallBack {
    Dialog dialog;
    Window window;
    Context context;
    public  boolean isShow;
    //回看播放器
    private HikRecordPlayer hikRecordPlayer;
    //获取数据
    private Intent intent;
    //文件路径
    private String url;
    //控制暂停和恢复的控件
    private ImageButton ibPlay;
    //加载视频的控件
    private TextureView tvReview;
    //显示当前进度的控件
    private TextView tvNow;
    //显示总时间的控件
    private TextView tvTotal;
    //拖动进度条
    private SeekBar sbReview;
    //计数器
    private int count;
    //开始播放的时间点
    private long playedTime;
    public DialogReview(final Context context) {
        dialog = new Dialog(context, R.style.nodarken_style);
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        window = dialog.getWindow();
        window.setContentView(R.layout.activity_review);
        this.context =context;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity= Gravity.TOP;
        lp.y=60;
        window.setAttributes(lp);
        sbReview = (SeekBar) window.findViewById(R.id.sb_review);
        //拖动进度条改变状态的监听器
        sbReview.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            //状态改变
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = seekBar.getProgress();
                playedTime = progress;
            }

            @Override
            //开始拖动
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            //停止拖动
            public void onStopTrackingTouch(SeekBar seekBar) {
//                hikRecordPlayer.playOnline(tvReview, playedTime, url, true);
                ibPlay.setBackground(context.getResources().getDrawable(R.drawable.photo_view_mid_pause));
            }
        });
        tvNow = (TextView) window.findViewById(R.id.tv_now);
        tvTotal = (TextView) window.findViewById(R.id.tv_total);
        hikRecordPlayer = new HikRecordPlayer();
        //回到上层界面的组件
        ImageButton ibReturn = (ImageButton) window.findViewById(R.id.ib_return);
        ibReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        tvReview = (TextureView) window.findViewById(R.id.tv_review);
        //加载视频的监听器
        tvReview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                hikRecordPlayer.playOnline(tvReview, 0, url, false);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        ibPlay = (ImageButton) window.findViewById(R.id.ib_play);
        count = 0;
        //暂停和恢复的点击响应
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                count++;
                //奇数次点击暂停播放，偶数次点击恢复播放
                if (count % 2 == 1) {
                    v.setBackground(context.getResources().getDrawable(R.drawable.photo_view_mid_play));
                    hikRecordPlayer.pausePlay();
                } else if (count != 0 && count % 2 == 0) {
                    v.setBackground(context.getResources().getDrawable(R.drawable.photo_view_mid_pause));
                    hikRecordPlayer.resumePlay();
                }
            }
        });

        window.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay();
            }
        });
        window.findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isShow=false;
                stopPlay();
            }
        });
    }

    public void cancel(){
        dialog.cancel();
        isShow=false;
    }
    public  void  show(String url){
        this.url = url;
        dialog.show();
        isShow=true;
    }

    private HikRecordPlayer mRecordPlayer;
    private boolean isPaused;
    private long mTotalTime;

    private void startPlay() {
        mRecordPlayer = new HikRecordPlayer();
        mRecordPlayer.setVideoCallBack(this);
        HikVideoModel videoModel = new HikVideoModel();
        videoModel.setTextureView(tvReview);
        videoModel.setUrl(url);
//        videoModel.setUrl("http://192.168.42.1/SMCAR/DOWNLOAD/tmp/SD0/DCIM/ch1_20170101_045431_0200.mp4");
//        videoModel.setPlaySound(false);
        videoModel.setHardDecode(true);
        videoModel.setPackageFormat(1); // 0:PS 1:MP4
        mRecordPlayer.playOnline(videoModel);
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (mRecordPlayer != null) {
            mRecordPlayer.stopPlay();
        }
    }

    @Override
    public void onVideoSuccess(int msgID, String msg) {
        switch (msgID) {
            case HikVideoConstant.PLAYER_START_PLAY_SUCCESS:
                if (mRecordPlayer == null) {
                    return;
                }
                long totalTime = mRecordPlayer.getTotalTime();
                tvTotal.setText(formatLongToTimeStr(totalTime));
                sbReview.setMax((int) totalTime);
                break;
            case HikVideoConstant.PLAYER_ON_DISPLAY:
                if (mRecordPlayer == null) {
                    return;
                }
                int playedTime = mRecordPlayer.getPlayedTime();
                tvNow.setText(formatLongToTimeStr((long) playedTime));
                sbReview.setProgress(playedTime);
                break;
        }
    }

    @Override
    public void onVideoFailure(int i, String s, int i1) {
        HikLog.infoLog("test111", i + " " + s + " " + i1);
    }

    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = l.intValue();

        if (second < 0)
            second = 0;

        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return (decimalFormat.format(hour) + ":" + decimalFormat.format(minute) + ":" + decimalFormat.format(second));
    }
}
