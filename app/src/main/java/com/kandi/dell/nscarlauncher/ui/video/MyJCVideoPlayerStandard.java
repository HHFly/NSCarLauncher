package com.kandi.dell.nscarlauncher.ui.video;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCFullScreenActivity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class MyJCVideoPlayerStandard extends JCVideoPlayerStandard{
    FruitAdapter adapter;


    public MyJCVideoPlayerStandard(Context context) {
        super(context);
        initView(context);

    }

    public MyJCVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    protected void initView(final Context context) {
//        fullscreenButton.setVisibility(View.GONE);
        adapter = new FruitAdapter(context, R.layout.list_item, VideoFragment.mData);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btNextLast(context,position);
            }
        });
        btn_list.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listlayout.getVisibility() == View.GONE){
                    listlayout.setVisibility(View.VISIBLE);
                    listlayout.animate().alpha(1f).setDuration(500).setListener(null);
                    listlayout.startAnimation(mShowAction);
                }else{
                    listlayout.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            listlayout.setVisibility(View.GONE);
                        }
                    });
                    listlayout.startAnimation(mHiddenAction);
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btNextLast(true,context);
            }
        });
        btn_last.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btNextLast(false,context);
            }
        });
        switch (VideoFragment.dataMode){
            case 1:
                mtitle.setText(context.getString(R.string.localmovie));
                break;
            case 2:
                mtitle.setText(context.getString(R.string.usbmovie));
                break;
            default:
                mtitle.setText(context.getString(R.string.localmovie));
                break;
        }
    }

    public void btNextLast(boolean isNextLast,Context context){
        backFullscreen();
        if(isNextLast){
            VideoFragment.position = VideoFragment.position + 1;
            if(VideoFragment.position < VideoFragment.mData.size()){
                JCVideoPlayer.WIFI_TIP_DIALOG_SHOWED =true;//关闭网络播放提示
                JCFullScreenActivity.startActivity(getContext(),
                        VideoFragment.mData.get(VideoFragment.position).url,
                        MyJCVideoPlayerStandard.class,
                        VideoFragment.mData.get(VideoFragment.position).title);
            }
        }else{
            VideoFragment.position = VideoFragment.position - 1;
            if(VideoFragment.position >= 0){
                JCVideoPlayer.WIFI_TIP_DIALOG_SHOWED =true;//关闭网络播放提示
                JCFullScreenActivity.startActivity(getContext(),
                        VideoFragment.mData.get(VideoFragment.position).url,
                        MyJCVideoPlayerStandard.class,
                        VideoFragment.mData.get(VideoFragment.position).title);
            }
        }
    }

    public void btNextLast(Context context,int position){
        backFullscreen();
        VideoFragment.position = position;
        if(VideoFragment.position >= 0){
            JCVideoPlayer.WIFI_TIP_DIALOG_SHOWED =true;//关闭网络播放提示
            JCFullScreenActivity.startActivity(getContext(),
                    VideoFragment.mData.get(VideoFragment.position).url,
                    MyJCVideoPlayerStandard.class,
                    VideoFragment.mData.get(VideoFragment.position).title);
        }
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        VideoFragment.position = VideoFragment.position + 1;
        if(VideoFragment.position < VideoFragment.mData.size()){
            JCVideoPlayer.WIFI_TIP_DIALOG_SHOWED =true;//关闭网络播放提示
            JCFullScreenActivity.startActivity(getContext(),
                    VideoFragment.mData.get(VideoFragment.position).url,
                    MyJCVideoPlayerStandard.class,
                    VideoFragment.mData.get(VideoFragment.position).title);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v, event);
        return  true;
    }

    class FruitAdapter extends ArrayAdapter{
        private final int resourceId;

        public FruitAdapter(Context context, int textViewResourceId, List<Mp3Info> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Mp3Info fruit = (Mp3Info) getItem(position); // 获取当前项的Fruit实例
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
            TextView fruitName = (TextView) view.findViewById(R.id.list_item);//获取该布局内的文本视图
            fruitName.setText(fruit.getTitle());
            return view;
        }
    }

}