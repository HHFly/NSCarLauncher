package com.example.dell.nscarlauncher.ui.music;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IKdAudioControlService;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MusicFragment extends BaseFragment {
    private GifImageView mGifImageView;
    private GifDrawable gifDrawable;
    private boolean isPlay;

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
    public static ImageButton bt_open, bt_play, bt_back, bt_next, bt_u, bt_music_model;
    @Override
    public int getContentResId() {
        return R.layout.fragment_music;
    }

    @Override
    public void findView() {
        bt_play=getView(R.id.bt_gif);
        music_progress_bar=getView(R.id.music_progress_bar);
          context=  getContext();
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_fm_left);
        setClickListener(R.id.iv_fm_right);
    }

    @Override
    public void initView() {


        initSeekBar();

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
            case R.id.iv_fm_left:
                musicBack();
                break;
            case R.id.iv_fm_right:
                musicNext();
                break;
        }
    }

    /*上一首*/
    private void musicBack(){
        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
            new Thread() {
                public void run() {
                    myHandler.sendMessage(myHandler.obtainMessage(MUSCI_BACK));
                }


            }.start();
        }
    }
/*下一首*/
private  void  musicNext(){
    if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
        new Thread() {
            public void run() {
                myHandler.sendMessage(myHandler.obtainMessage(MUSIC_NEXT));
            }

            ;
        }.start();
    }
}
    /*进度条*/
    private void initSeekBar(){
        music_progress_bar.setProgress(0);
        music_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                int totaltime = (int) Math.ceil(DialogLocalMusic.data.get(DialogLocalMusic.musicID).duration);
//                int num = (int) Math.ceil(Math.round((float) progress / 100.0 * totaltime));
//
//                if (PlayerService.is_start_speed) {
//                    circle_image.roatateStart();
//                    bt_play.setBackgroundResource(R.drawable.fragment_music_pause);
//                    flag_play = true;
//                }
//
//                Intent i = new Intent(getActivity(), PlayerService.class);
//                i.putExtra("progress", num);
//                i.putExtra("MSG", FlagProperty.PROGRESS_CHANGE);
//                getActivity().startService(i);
//                flag_drag = false;
//                flag_first = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                flag_drag = true;

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                FragmentMusic.progress = progress;

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
    public  void setMusicInfo(String songname, String singer) {
       setTvText(R.id.tv_bt_songname,songname);
       setTvText(R.id.tv_bt_singer,("").equals(singer)?"":"-"+singer);
    }




    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MUSIC_CHANGE:
                        getView(R.id.bt_gif).performClick();
                        break;
                    case MUSIC_BLUETOOTH_CLOSE:

                        break;

                    case MUSIC_BLUETOOTH_OPEN:

                        break;
                    case MUSCI_BACK:
                        btservice.btAvrLast();
                        break;
                    case MUSIC_NEXT:
                        btservice.btAvrNext();
                        break;
                    default:
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        ;
    };
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


}
