package com.example.dell.nscarlauncher.ui.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.Activity.BaseActivity;
import com.example.dell.nscarlauncher.ui.music.model.Mp3Info;

import fm.jiecao.jcvideoplayer_lib.JCFullScreenActivity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class JCPlayerActivity extends BaseActivity {
    private JCVideoPlayerStandard jcVideoPlayerStandard ;
    String path;
    String name;
    private Mp3Info data;
    @Override
    public int getContentViewResId() {
        return R.layout.activity_jcplayer;
    }

    @Override
    public void findView() {
        jcVideoPlayerStandard=getView(R.id.jiecao_player_view);
    }

    @Override
    public void getIntentParam(Bundle bundle) {

    }

    @Override
    public void initView() {

        data= (Mp3Info) getIntent().getSerializableExtra("Mp3Info");
         path = data.url; // 视频地址
         name = data.displayName; // 视频名称
        JCFullScreenActivity.startActivity(this,
                path,
                JCVideoPlayerStandard.class,
                "嫂子别摸我");

        // 视频的回退按钮设置点击事件
        jcVideoPlayerStandard.backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                jcVideoPlayerStandard.release(); // 释放视频
                JCPlayerActivity.this.finish(); // 结束当前界面
            }
        });

    }

    @Override
    protected void onDestroy() {
        JCVideoPlayer.releaseAllVideos();
        super.onDestroy();
    }
}
