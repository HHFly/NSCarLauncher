package com.kandi.dell.nscarlauncher.ui_portrait.video;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.SeekBar;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.TimeUtils;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui_portrait.music.model.Mp3Info;

import fm.jiecao.jcvideoplayer_lib.JCMediaManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class VideoFragment extends BaseFragment {
    private VideoPlayStandard hailinVideoPlayer;
    int currentTime,videoTime,Progress;
    SeekBar video_progress_bar;
    boolean isPause,flag_drag,flag_first;
    int music_model = 1;
    @Override
    public int getContentResId() {
        return R.layout.fragment_video_por;
    }

    @Override
    public void findView() {
        hailinVideoPlayer=getView(R.id.videoplayer);
        video_progress_bar = getView(R.id.video_progress_bar);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        setClickListener(R.id.iv_video_left);
        setClickListener(R.id.iv_video_right);
        setClickListener(R.id.ctl_video_center);
        setClickListener(R.id.iv_video_list);
    }

    @Override
    public void initView() {
        setmType(FragmentType.VIDEO);
        initSeekBar();
    }

    @Override
    public void onClick(View v) {
        if (TimeUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()){
            case R.id.iv_return:
                stopVideo();
                handler.sendEmptyMessage(2);
                App.get().getCurActivity().jumpFragment(FragmentType.APPLICATION);
                break;
            case R.id.iv_video_list:
                App.get().getCurActivity().getDialogVideo().show();
                break;
            case R.id.ctl_video_center:
                controllVideo();
                break;
            case R.id.iv_video_left:
                VideoModel.getPrevMusic(getContext(),music_model);
                isPause=false;
                break;
            case R.id.iv_video_right:
                VideoModel.getNextVideo(getContext(),music_model);
                isPause=false;
                break;
        }
    }
    private void  controllVideo(){
        if(!isPause){
            pauseVideo();
        }else {
            resumeVideo();
        }

    }
    public void pauseVideo() {
        try {
            if (JCMediaManager.instance().mediaPlayer!=null&&JCMediaManager.instance().mediaPlayer.isPlaying()) {
                isPause =!isPause;
                JCMediaManager.instance().mediaPlayer.pause();
                handler.sendEmptyMessage(2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void resumeVideo() {
        try {
            if (JCMediaManager.instance().mediaPlayer!=null&&!JCMediaManager.instance().mediaPlayer.isPlaying()){
                isPause =!isPause;
                JCMediaManager.instance().mediaPlayer.start();
                handler.sendEmptyMessage(2);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void stopVideo() {
//        if (HaiLinMediaManager.instance().mediaPlayer!=null&&HaiLinMediaManager.instance().mediaPlayer.isPlaying()) {
        isPause = true;
        JCVideoPlayer.releaseAllVideos();
        setTvText(R.id.tv_video_name,"");
        setTvText(R.id.video_current_time,"00:00");
        setTvText(R.id.video_total_time,"00:00");
        initSeekBar();
//        }
    }
    public void play(Mp3Info data) {
        try {
//            HaiLinMediaManager.instance().mediaPlayer.reset();

            // 设置需要播放的视频
//            Uri uri = Uri.parse(url);
//            HaiLinMediaManager.instance().mediaPlayer.setDataSource(getApplicationContext(), uri);
//            HaiLinMediaManager.instance().mediaPlayer.prepare();
//            // 播放
//            HaiLinMediaManager.instance().mediaPlayer.start();
            hailinVideoPlayer.setUp(data.url,data.title);
            hailinVideoPlayer.start();
            currentTime=0;
            setMusicInfo(App.get().getCurActivity().getDialogVideo().data.get(App.get().getCurActivity().getDialogVideo().musicDiverID).displayName);
            isPause = false;
            handler.sendEmptyMessage(1);
            handler.sendEmptyMessage(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*初始化进度条*/
    private void initSeekBar(){
        video_progress_bar.setProgress(0);
        video_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startProgress(seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//              App.get().getmPresentation().cancelProgress();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Progress = progress;
            }
        });
        hailinVideoPlayer.setAutoCompletion(new VideoPlayStandard.AutoCompletion() {
            @Override
            public void onAutoCompletion() {
                VideoModel.getNextVideo(getContext(),music_model);
            }

            @Override
            public void onError(int what, int extra) {
                isPause=false;
                setViewSelected(R.id.ctl_video_center,isPause);
                getView(R.id.ctl_video_center).setBackgroundResource(  isPause?R.mipmap.ic_music_play:R.mipmap.ic_music_home_stop);

            }
        });
    }
    public void startProgress(SeekBar seekBar){
        if (hailinVideoPlayer.mCurrentState != hailinVideoPlayer.CURRENT_STATE_PLAYING &&
                hailinVideoPlayer.mCurrentState != hailinVideoPlayer.CURRENT_STATE_PAUSE) return;
        hailinVideoPlayer.startProgressTimer();
        ViewParent vpup = hailinVideoPlayer.getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }

        int time = seekBar.getProgress() * hailinVideoPlayer.getDuration() / 100;
        JCMediaManager.instance().mediaPlayer.seekTo(time);
    }
    // 设置歌曲进度条信息
    public  void setMusicProgress(int time) {
        time /= 1000; // 得到的是毫秒级别的
        if (videoTime != time && !flag_drag) {
            videoTime = time;
            int totaltime = (int) Math.ceil(App.get().getCurActivity().getDialogVideo().data.get(App.get().getCurActivity().getDialogVideo().musicDiverID).duration);
            totaltime /= 1000;
            if (time > totaltime) {
                time = totaltime; // 避免毫秒级时间产生时间误差
            }
            if (totaltime > 0) {
                int progress = (int) Math.ceil(time * 100 / totaltime);
                if (flag_first) {
                    if (progress > Progress) {
                        video_progress_bar.setProgress(progress);
                        flag_first = false;
                    }
                } else {
                    video_progress_bar.setProgress(progress);
                }
                setTvText(R.id.video_current_time,getTime(time / 60) + ":" + getTime(time % 60));
                setTvText(R.id.video_total_time,getTime(totaltime / 60) + ":" + getTime(totaltime % 60));
//                setTvText(R.id.video_controll_tv_name,mData.get(position).displayName);
            }
        }
    }
    // 设置歌曲信息
    public  void setMusicInfo(String songname) {
        setTvText(R.id.tv_video_name,songname);
    }
    // 时间格式化为00
    public static String getTime(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }

    /**
     * handler用来接收消息，来发送广播更新播放时间
     */
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if(JCMediaManager.instance().mediaPlayer != null) {
                    try {
                        currentTime = JCMediaManager.instance().mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                       setMusicProgress(currentTime);
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }catch (Exception e){
                        handler.sendEmptyMessageDelayed(1, 1000);


                    }

                }
            }else if(msg.what == 2){
                getView(R.id.ctl_video_center).setBackgroundResource(  isPause?R.mipmap.ic_music_home_stop:R.mipmap.ic_music_play);
            }
        };
    };
}
