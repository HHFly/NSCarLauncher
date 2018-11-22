package com.kandi.dell.nscarlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.common.util.LogUtils;
import com.kandi.dell.nscarlauncher.ui.bluetooth.BTMusicFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.music.model.MusicModel;
import com.kandi.dell.nscarlauncher.ui.phone.PhoneFragment;

import static com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity.homePagerActivity;


public class CarMFLReceiver extends BroadcastReceiver {
    public static  String ACTION_WHEEL_MODE ="com.kangdi.BroadCast.WheelMode";//多功能模式
    public static String ACTION_WHEEL_VOICE ="com.kangdi.BroadCast.WheelVoice";//语音
    public static  String ACTION_WHEEL_MUSIC_PREV ="com.kangdi.BroadCast.WheelMusicPrev";//上一首
    public static String ACTION_WHEEL_MUSIC_NEXT="com.kangdi.BroadCast.WheelMusicNext";//下一首
    public static String ACTION_WHEEL_CALL="com.kangdi.BroadCast.WheelCall";//接听
    public static   String ACTION_WHEEL_HANGUP="com.kangdi.BroadCast.WheelHangup";//挂断

    private @FragmentType int nowFragment=FragmentType.FM;//模式切换
    private boolean isMute;//是否静音
    private int BlueVolume =7;
     @Override
    public void onReceive(Context context, Intent intent) {
        //多功能模式切换
        if (intent.getAction().equals(ACTION_WHEEL_MODE)) {
            LogUtils.log(ACTION_WHEEL_MODE);
            if( SystemProperties.getInt("sys.kd.revers",0)==0) {
                HomePagerActivity.jumpFragment(nowFragment);
              switch (nowFragment){
                  case FragmentType.FM:
                      nowFragment=FragmentType.BTMUSIC;
                      break;
                  case FragmentType.BTMUSIC:
                      nowFragment=FragmentType.MUSIC;
                    break;
                  case  FragmentType.MUSIC:
                      nowFragment=FragmentType.PHONE;
                      break;
                  case FragmentType.PHONE:
                      nowFragment=FragmentType.VIDEO;
                  case FragmentType.VIDEO:
                      nowFragment =FragmentType.FM;
                      break;
              }

            }
        }
        //语音
        if (intent.getAction().equals(ACTION_WHEEL_VOICE)) {
            LogUtils.log(ACTION_WHEEL_VOICE);
            if( SystemProperties.getInt("sys.kd.revers",0)==0) {

            }
        }
        //上一首
        if (intent.getAction().equals(ACTION_WHEEL_MUSIC_PREV)) {
            LogUtils.log(ACTION_WHEEL_MUSIC_PREV);
            if( SystemProperties.getInt("sys.kd.revers",0)==0) {
               if(BTMusicFragment.isPlay){
                   BTMusicFragment.myHandler.sendEmptyMessage(6);
               }
               if(homePagerActivity.getMusicFragment().flag_play){
                   if( homePagerActivity.getDialogLocalMusic().data.size()>0) {

                       if ( homePagerActivity.getMusicFragment().music_model == 2) { // 单曲循环模式不变换音乐图片
                           if(homePagerActivity.getMusicFragment().circle_image!=null)
                               homePagerActivity.getMusicFragment().circle_image.resetRoatate();
                       } else { // 其他模式
                           // circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_NEXT)));
                       }

                       MusicModel.getPrevMusic(context, homePagerActivity.getMusicFragment().music_model);
                   }
               }
               if(HomePagerActivity.homePagerActivity.getFmFragment().isPlay){
                   App.pagerOneHnadler.sendEmptyMessage(HandleKey.FMPREV);
               }
            }
        }
        //下一首
        if (intent.getAction().equals(ACTION_WHEEL_MUSIC_NEXT)) {
            LogUtils.log(ACTION_WHEEL_MUSIC_NEXT);
            if( SystemProperties.getInt("sys.kd.revers",0)==0) {
                if(BTMusicFragment.isPlay){
                    BTMusicFragment.myHandler.sendEmptyMessage(7);
                }
                if(homePagerActivity.getMusicFragment().flag_play){
                    if(homePagerActivity.getDialogLocalMusic().data.size()>0) {

                        if ( homePagerActivity.getMusicFragment().music_model == 2) { // 单曲循环模式不变换音乐图片
                            if(homePagerActivity.getMusicFragment().circle_image!=null)
                            homePagerActivity.getMusicFragment().circle_image.resetRoatate();
                        } else { // 其他模式
                            // circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_NEXT)));
                        }

                        MusicModel.getNextMusic(context,  homePagerActivity.getMusicFragment().music_model);
                    }
                }
                if(HomePagerActivity.homePagerActivity.getFmFragment().isPlay){
                    App.pagerOneHnadler.sendEmptyMessage(HandleKey.FMNEXT);
                }
            }
        }
        //接听
        if (intent.getAction().equals(ACTION_WHEEL_CALL)) {
            if( FlagProperty.flag_phone_ringcall ) {
                FlagProperty.flag_phone_ringcall=false;
                LogUtils.log(ACTION_WHEEL_CALL);
                HomePagerActivity.jumpFragment(FragmentType.PHONE);
                FlagProperty.flag_phone_incall_click = true;
                PhoneFragment.answerPhone();
                PhoneFragment.phoneStart();
            }

        }
        //挂断
        if (intent.getAction().equals(ACTION_WHEEL_HANGUP)) {
            LogUtils.log(ACTION_WHEEL_HANGUP);
            FlagProperty.flag_phone_ringcall = false;
            PhoneFragment.hangDownphone();
        }

    }
}
