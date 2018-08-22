package com.example.dell.nscarlauncher.ui.bluetooth;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.MediaController;
import android.widget.SeekBar;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class BTMusicFragment extends BaseFragment {
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
    @Override
    public int getContentResId() {
        return R.layout.fragment_bt_music;
    }

    @Override
    public void findView() {
        mGifImageView=getView(R.id.bt_gif);
        music_progress_bar=getView(R.id.music_progress_bar);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_gif);
        setClickListener(R.id.iv_fm_left);
        setClickListener(R.id.iv_fm_right);
    }

    @Override
    public void initView() {
        initGif();

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
        requestAudioFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_gif:
                gifPlay(isPlay);
                isPlay=!isPlay;
                break;
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
    /*获取全局模块*/
    private void  getService(){
        if(audioservice==null) {
            audioservice = App.get().getAudioservice();
        }
        if(audioManager==null) {
            audioManager = App.get().getAudioManager();
        }
        if(btservice==null) {
            btservice = App.get().getBtservice();
        }
    }



    // 设置歌曲信息
    public  void setMusicInfo(String songname, String singer) {
       setTvText(R.id.tv_bt_songname,songname);
       setTvText(R.id.tv_bt_singer,("").equals(singer)?"":"-"+singer);
    }

    /*初始化gif控制*/
    private void initGif(){
        try {
            gifDrawable = new GifDrawable(getResources(), R.mipmap.bt_music);
            gifDrawable.stop();
            mGifImageView.setImageDrawable(gifDrawable);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void gifPlay(boolean isPlay){
        if(isPlay){
            gifDrawable.start();
        }else {
            gifDrawable.stop();
        }
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
     * 蓝牙音乐监听器
     */
    public static AudioManager.OnAudioFocusChangeListener afBTChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            switch (i){
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.d("audioTest", "btmusic loss transient");

                    try {
                        btservice.btAvrPause();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                case  AudioManager.AUDIOFOCUS_GAIN:
                    Log.d("audioTest", "btmusic gain");
                    try {
                        if (!audioservice.isDuringNavi()) {
                            btservice.btAvrPlay();
                        }
                    }catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case  AudioManager.AUDIOFOCUS_LOSS:
                    try {
                        btservice.btAvrStop();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case   AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    try {
                        btservice.btAvrStop();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };

    private void requestAudioFocus() {



        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {

            FlagProperty.flag_bluetooth = true;

            //if (!flag_bluetooth_music) {//打开开关按钮

            if (audioManager.requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                return;
            }

            setMusicInfo("", "");

            Log.d("musictest", "btmusic open");
            //}

        } else {
           setVisibilityGone(R.id.bt_mic_null,true);
        }
    }
}