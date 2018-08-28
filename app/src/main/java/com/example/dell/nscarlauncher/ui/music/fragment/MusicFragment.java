package com.example.dell.nscarlauncher.ui.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IKdAudioControlService;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.example.dell.nscarlauncher.ui.fm.FMAdapter;
import com.example.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.example.dell.nscarlauncher.ui.music.adapter.MusicAdapter;
import com.example.dell.nscarlauncher.ui.music.model.Mp3Info;
import com.example.dell.nscarlauncher.ui.music.model.MusicModel;
import com.example.dell.nscarlauncher.ui.music.Service.PlayerService;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MusicFragment extends BaseFragment {

    private final static int DIRECTION_PREV = 1; // 向前切歌
    private final static int DIRECTION_NEXT = 2; // 向后切歌
    private final static int MUSIC_CHANGE = 3; // 切歌时进行瞬间暂停状态
    private final static int MUSIC_BLUETOOTH_CLOSE = 4; // 蓝牙音乐关闭
    private final static int MUSIC_BLUETOOTH_OPEN = 5; // 蓝牙音乐打开
    private final static int MUSCI_BACK = 6; // 蓝牙音乐上一首
    private final static int MUSIC_NEXT = 7; // 蓝牙音乐下一首
    static int music_model = 1; // 音乐播放循环模式
    static SeekBar music_progress_bar; // 音乐播放进度条
    static int progress = 0; // 记录进度
    static int music_time = 0; // 记录歌曲时间，若无变化，则不更新界面
    static boolean flag_drag = false; // 是否拖动歌曲进度条
    public static boolean flag_first = false;
    static AudioManager audioManager;
    static IKdAudioControlService audioservice ;
    static IKdBtService btservice;
    static boolean system_flag, am_flag,flag_play;
    static Context context;
    public static ImageView bt_open, bt_play, bt_back, bt_next, bt_u, bt_music_model;
    public static TextView tv_music_songname, tv_music_singer, music_current_time, music_total_time;
    public  static ImageView circle_image;
  private   List<Mp3Info> mData = new ArrayList<>();//数据源
    private MusicAdapter mAdapter;
    @Override
    public int getContentResId() {
        return R.layout.fragment_music;
    }

    @Override
    public void findView() {
        bt_play=getView(R.id.music_play);
        music_progress_bar=getView(R.id.music_progress_bar);
        music_current_time =getView(R.id.current_time);
        music_total_time=getView(R.id.music_total_time);
        tv_music_songname=getView(R.id.tv_bt_songname);
        tv_music_singer =getView(R.id.tv_bt_singer);
        circle_image =getView(R.id.circle_image);
        bt_music_model=getView(R.id.iv_music_mode);
        context=  getContext();
    }

    @Override
    public void setListener() {
        setClickListener(R.id.music_left);
        setClickListener(R.id.music_ringt);
        setClickListener(R.id.music_play);

    }

    @Override
    public void initView() {


        initSeekBar();
        getMusicData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getService();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.music_play:
                play();
                break;
            case R.id.music_left:

                break;
            case R.id.music_ringt:

                break;
            case R.id.iv_music_mode:
                setMode();
                break;
        }
    }
  /*音乐模式*/
  private  void setMode(){
      try {
          if (music_model == 1) {
//              bt_music_model.setBackgroundResource(R.drawable.music_model_singlecycle);
              music_model = 2;
          } else if (music_model == 2) {
//              bt_music_model.setBackgroundResource(R.drawable.music_model_randomcycle);
              music_model = 3;
          } else if (music_model == 3) {
//              bt_music_model.setBackgroundResource(R.drawable.music_model_listcycle);
              music_model = 1;
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

/*音乐播放*/
private  void  play(){
    if (!flag_play) {
        if (audioManager.requestAudioFocus(afChangeListener, 12,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                && audioManager.requestAudioFocus(afSystemChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = true;
            am_flag = true;
            if (DialogLocalMusic.data.size() > 0) {
//                circle_image.roatateStart();
                bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                broadcastMusicInfo(getActivity(), FlagProperty.PLAY_MSG);
                flag_play = true;
            }
        }
    } else {
        if (audioManager.abandonAudioFocus(afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                && audioManager.abandonAudioFocus(
                afSystemChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = false;
            am_flag = false;

//            circle_image.roatatePause();
            bt_play.setBackgroundResource(R.mipmap.ic_play);
            broadcastMusicInfo(getActivity(), FlagProperty.PAUSE_MSG);
            flag_play = false;
        }
    }
}


    /*进度条*/
    private void initSeekBar(){
        music_progress_bar.setProgress(0);
        music_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int totaltime = (int) Math.ceil(DialogLocalMusic.data.get(DialogLocalMusic.musicID).duration);
                int num = (int) Math.ceil(Math.round((float) progress / 100.0 * totaltime));

                if (PlayerService.is_start_speed) {

                    bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                    flag_play = true;
                }

                Intent i = new Intent(getActivity(), PlayerService.class);
                i.putExtra("progress", num);
                i.putExtra("MSG", FlagProperty.PROGRESS_CHANGE);
                getActivity().startService(i);
                flag_drag = false;
                flag_first = true;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                flag_drag = true;

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MusicFragment.progress = progress;

            }
        });
    }
    /*获取全局模块*/
    private void  getService(){
        audioservice=App.get().getAudioservice();
        audioManager=App.get().getAudioManager();
        btservice =App.get().getBtservice();
    }



    // 设置歌曲信息
    public static void setMusicInfo(String songname, String singer) {
        if (tv_music_songname != null) {
            tv_music_songname.setText(songname);
            if (!("").equals(singer)) {
                tv_music_singer.setText("- " + singer);
            } else {
                tv_music_singer.setText("");
            }
        }
    }





    /**
     * 本地音乐监听器
     */
    public static AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d("audioTest", "12 gain");
                am_flag = true;
                if (system_flag) {
                    Log.d("audioTest", "play music");
                    if (DialogLocalMusic.data.size() > 0) {

                        bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                        broadcastMusicInfo(context, FlagProperty.PLAY_MSG);
                        flag_play = true;
                    }
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d("audioTest", "12 loss");
                am_flag = false;

                bt_play.setBackgroundResource(R.mipmap.ic_play);
                broadcastMusicInfo(context, FlagProperty.PAUSE_MSG);
                flag_play = false;
            }

        }
    };

    /**
     * 本地系统音乐监听器
     */
    public static AudioManager.OnAudioFocusChangeListener afSystemChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Log.d("audioTest", "3 loss transient");
                system_flag = false;

                bt_play.setBackgroundResource(R.mipmap.ic_play);
                broadcastMusicInfo(context, FlagProperty.PAUSE_MSG);
                flag_play = false;
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d("audioTest", "3 gain");
                system_flag = true;
                if (am_flag) {
                    Log.d("audioTest", "playmusic");
                    if (DialogLocalMusic.data.size() > 0) {

                        bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                        broadcastMusicInfo(context, FlagProperty.PLAY_MSG);
                        flag_play = true;
                    }
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d("audioTest", "3 loss");
                system_flag = false;

                bt_play.setBackgroundResource(R.mipmap.ic_play);
                broadcastMusicInfo(context, FlagProperty.PAUSE_MSG);
                flag_play = false;
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Log.d("audioTest", "3 loss transient can duck");
                system_flag = false;

                bt_play.setBackgroundResource(R.mipmap.ic_play);
                broadcastMusicInfo(context, FlagProperty.PAUSE_MSG);
                flag_play = false;
            }

        }
    };
    // 发送音乐变更信息
    public static void broadcastMusicInfo(Context context, int msg) {
        Intent i = new Intent(context, PlayerService.class);
        i.putExtra("MSG", msg);
        context.startService(i);
    }
    // 开始播放音乐
    public static void listStartPlayMusic() {
        if (audioManager.requestAudioFocus(afChangeListener, 12,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                && audioManager.requestAudioFocus(afSystemChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = true;
            am_flag = true;

            bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
            flag_play = true;
        }
    }
    // 设置歌曲进度条信息
    public static void setMusicProgress(int time) {
        time /= 1000; // 得到的是毫秒级别的
        if (music_time != time && !flag_drag) {
            music_time = time;
            int totaltime = (int) Math.ceil(DialogLocalMusic.data.get(DialogLocalMusic.musicID).duration);
            totaltime /= 1000;
            if (time > totaltime) {
                time = totaltime; // 避免毫秒级时间产生时间误差
            }
            if (totaltime > 0) {
                int progress = (int) Math.ceil(time * 100 / totaltime);
                if (flag_first) {
                    if (progress > MusicFragment.progress) {
                        music_progress_bar.setProgress(progress);
                        flag_first = false;
                    }
                } else {
                    music_progress_bar.setProgress(progress);
                }
                music_current_time.setText("" + getTime(time / 60) + ":" + getTime(time % 60));
                music_total_time.setText("" + getTime(totaltime / 60) + ":" + getTime(totaltime % 60));
            }
        }
    }

    // 时间格式化为00
    public static String getTime(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }
/*初始化本地音乐数据*/
  private  void getMusicData(){
      mData =DialogLocalMusic.data;

        initRvAdapter(mData);
  }

    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<Mp3Info> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView);
            mAdapter =new MusicAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {

                @Override
                public void onClickMusic(Mp3Info data, int Pos) {
                    DialogLocalMusic.musicID=Pos;
                    if (flag_play) {
                        bt_play.performClick();
                    }
                    play();
                }


            });

        }else {
            mAdapter.notifyDataSetChanged();
        }
        setViewVisibilityGone(R.id.item_music_null,data==null||data.size()==0);
    }
}
