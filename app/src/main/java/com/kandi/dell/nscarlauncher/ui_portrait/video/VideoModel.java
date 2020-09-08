package com.kandi.dell.nscarlauncher.ui_portrait.video;

import android.content.Context;

import com.kandi.dell.nscarlauncher.app.App;



public class VideoModel {
    public static final int LIST_LOOP = 1;//列表循环
    public static final int SINGLE_LOOP = 2;         //单曲循环
    public static final int RANDOM_PLAY = 3;         //随机循环
    // 获取下首歌曲
    public static void getNextVideo(Context context, int model) {
        if(App.get().getCurActivity().getDialogVideo().data.size()>0) {
            if (model == LIST_LOOP) {
                if (App.get().getCurActivity().getDialogVideo().getPosition() >= (App.get().getCurActivity().getDialogVideo().data.size() - 1) ) {
                    App.get().getCurActivity().getDialogVideo().setPosition(0);
                } else {
                    App.get().getCurActivity().getDialogVideo().setPosition(App.get().getCurActivity().getDialogVideo().getPosition()+1);
                }
            } else if (model == SINGLE_LOOP) {

            } else if (model == RANDOM_PLAY) {
                getRandom(App.get().getCurActivity().getDialogVideo().data.size());
            }
//            App.get().getmPresentation().startVideo((App.get().getCurActivity().getDialogVideo().data.get((App.get().getCurActivity().getDialogVideo().getPosition()));
            App.get().getCurActivity().getVideoFragment().play(App.get().getCurActivity().getDialogVideo().data.get((App.get().getCurActivity().getDialogVideo().getPosition())));
        }
    }

    //获取上首歌曲
    public static void getPrevMusic(Context context, int model) {

        int ID =App.get().getCurActivity().getDialogVideo().getPosition();
        if(App.get().getCurActivity().getDialogVideo().data.size()>0) {
            if (model == LIST_LOOP) {
                if (App.get().getCurActivity().getDialogVideo().getPosition() == 0) {
                    App.get().getCurActivity().getDialogVideo().setPosition((App.get().getCurActivity().getDialogVideo().data.size()-1));
                } else {
                    App.get().getCurActivity().getDialogVideo().setPosition((App.get().getCurActivity().getDialogVideo().getPosition()-1));
                }
            } else if (model == SINGLE_LOOP) {

            } else if (model == RANDOM_PLAY) {
                getRandom(App.get().getCurActivity().getDialogVideo().data.size());
            }
//            App.get().getmPresentation().startVideo((App.get().getCurActivity().getDialogVideo().data.get((App.get().getCurActivity().getDialogVideo().getPosition()));
            App.get().getCurActivity().getVideoFragment().play(App.get().getCurActivity().getDialogVideo().data.get((App.get().getCurActivity().getDialogVideo().getPosition())));
        }
    }
    //获取随机的一首歌曲
    public static void getRandom(int sum){
        int num = 0;
        do {
            num = (int) (Math.random()*sum);
        } while (num == App.get().getCurActivity().getDialogVideo().getPosition() && sum > 1);
        App.get().getCurActivity().getDialogVideo().setPosition(num);
    }
}
