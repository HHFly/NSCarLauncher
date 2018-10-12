package com.kandi.dell.nscarlauncher.ui.video;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import fm.jiecao.jcvideoplayer_lib.JCFullScreenActivity;
import fm.jiecao.jcvideoplayer_lib.JCMediaManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class MyJCVideoPlayerStandard extends JCVideoPlayerStandard {

    public MyJCVideoPlayerStandard(Context context) {
        super(context);
        initView(context);
    }

    public MyJCVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    protected void initView(Context context) {
        fullscreenButton.setVisibility(View.GONE);
    }

    @Override
    public void onAutoCompletion() {
        //make me normal first
        if (JC_BURIED_POINT != null && JCMediaManager.instance().listener == this) {
            if (mIfCurrentIsFullscreen) {
                JC_BURIED_POINT.onAutoCompleteFullscreen(mUrl, mObjects);
            } else {
                JC_BURIED_POINT.onAutoComplete(mUrl, mObjects);
            }
        }
        setStateAndUi(CURRENT_STATE_AUTO_COMPLETE);
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        finishFullscreenActivity();
        if (IF_FULLSCREEN_FROM_NORMAL) {//如果在进入全屏后播放完就初始化自己非全屏的控件
            IF_FULLSCREEN_FROM_NORMAL = false;
            JCMediaManager.instance().lastListener.onAutoCompletion();
        }
        JCMediaManager.instance().lastListener = null;
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        VideoFragment.position = VideoFragment.position + 1;
        if(VideoFragment.position < VideoFragment.mData.size()){
            JCVideoPlayer.WIFI_TIP_DIALOG_SHOWED =true;//关闭网络播放提示
            JCFullScreenActivity.startActivity(VideoFragment.context,
                    VideoFragment.mData.get(VideoFragment.position).url,
                    MyJCVideoPlayerStandard.class,
                    VideoFragment.mData.get(VideoFragment.position).title);
        }
    }

    private static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    releaseAllVideos();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (JCMediaManager.instance().mediaPlayer.isPlaying()) {
                        JCMediaManager.instance().mediaPlayer.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };

}