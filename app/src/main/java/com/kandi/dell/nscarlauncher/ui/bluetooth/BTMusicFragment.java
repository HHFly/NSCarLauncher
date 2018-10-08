package com.kandi.dell.nscarlauncher.ui.bluetooth;

import android.media.AudioManager;
import android.os.Handler;
import android.os.IKdAudioControlService;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class BTMusicFragment extends BaseFragment {
    private GifImageView mGifImageView;
    public static GifDrawable gifDrawable;
    public static boolean isPlay;

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
    public static IKdBtService btservice;
    public static TextView  music_current_time,tv_bt_music_songname,tv_bt_music_singer,music_total_time,bt_blueSet;
    public  static ImageView iv_bt_stop;
    public static RelativeLayout NullView ;//空界面
    @Override
    public int getContentResId() {
        return R.layout.fragment_bt_music;
    }

    @Override
    public void findView() {
        mGifImageView=getView(R.id.bt_gif);
        music_progress_bar=getView(R.id.bt_music_progress_bar);
        music_current_time =getView(R.id.bt_music_current_time);
        tv_bt_music_singer=getView(R.id.tv_bt_singer);
        tv_bt_music_songname=getView(R.id.tv_bt_songname);
        music_total_time=getView(R.id.btmusic_total_time);
        NullView =getView(R.id.bt_mic_null);
        iv_bt_stop= getView(R.id.iv_bt_stop);
        bt_blueSet= getView(R.id.bt_blueSet);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_gif);
        setClickListener(R.id.iv_fm_left);
        setClickListener(R.id.iv_fm_right);
        setClickListener(R.id.iv_bt_stop);
        setClickListener(R.id.bt_blueSet);
    }

    @Override
    public void initView() {
        initGif();
        initSeekBar();

        setVisibilityGone(R.id.bt_mic_null,!FlagProperty.flag_bluetooth);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void Resume() {
        getService();
        requestAudioFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_gif:
                gifPlay(isPlay);


                break;
            case R.id.iv_fm_left:
                musicBack();
                break;
            case R.id.iv_fm_right:
                musicNext();
                break;
            case R.id.iv_bt_stop:
                musicPlay();
                break;
            case R.id.bt_blueSet:
                HomePagerActivity.jumpFragment(FragmentType.BTSET);
                break;
        }
    }

    /*暂停*/
    public  static void musicPause(){
        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
            if (App.get().getAudioManager().abandonAudioFocus(afBTChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                new Thread() {
                    public void run() {
                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
                    }


                }.start();
            }
        }
    }
    /*播放*/
    public static void musicPlay(){
        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
            if (App.get().getAudioManager().requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                new Thread() {
                    public void run() {
//                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));

                        MusicFragment.stopView();

                    }


                }.start();
            }
        }
    }
    /*上一首*/
    public static void musicBack(){

        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
            if (App.get().getAudioManager().requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                new Thread() {
                    public void run() {
                        myHandler.sendMessage(myHandler.obtainMessage(MUSCI_BACK));
                    }


                }.start();

            }
        }
    }
/*下一首*/
public static void  musicNext(){
    if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
        if (App.get().getAudioManager().requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            new Thread() {
                public void run() {
                    myHandler.sendMessage(myHandler.obtainMessage(MUSIC_NEXT));
                }

                ;
            }.start();
        }
    }
}
    /*进度条*/
    private void initSeekBar(){
        music_progress_bar.setProgress(0);
        music_progress_bar.setEnabled(false);
    }
    // 设置蓝牙音乐进度条信息
    public static void setBlueMusicProgress(int time) {
//        time /= 1000;
        int total_time = BlueMusicBroadcoast.music_total_time;
        if (time > total_time) {
            time = total_time;
        }
        if(total_time!=0) {
            progress = (int) Math.ceil(time * 100 / total_time);
        }
        if(music_progress_bar!=null) {
            music_progress_bar.setProgress(progress);
            music_current_time.setText("" + getTime(time / 60) + ":" + getTime(time % 60));
            music_total_time.setText(getTime(total_time / 60) + ":" + getTime(total_time % 60));
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
    public static void setMusicInfo(String songname, String singer) {
        if (tv_bt_music_songname != null) {
            tv_bt_music_songname.setText(songname);
            if (!("").equals(singer)) {
                tv_bt_music_singer.setText("- " + singer);
            } else {
                tv_bt_music_singer.setText("");
            }
        }
    }


    /*初始化gif控制*/
    private void initGif(){
        try {
            gifDrawable = new GifDrawable(getResources(), R.mipmap.bt_music);
            if (isPlay) {
//                gifDrawable.start();
                iv_bt_stop.setVisibility(View.GONE);
            } else {
//                gifDrawable.stop();
                iv_bt_stop.setVisibility(View.VISIBLE);
            }
//            mGifImageView.setImageDrawable(gifDrawable);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*开启gif*/
    public  static  void startGif(){
        if(gifDrawable!=null) {
//            gifDrawable.start();
            isPlay=true;
            iv_bt_stop.setVisibility(View.GONE);
        }
    }
    /*关闭gif*/
    public  static  void stopGif(){
        if(gifDrawable!=null) {
//            gifDrawable.stop();
            isPlay=false;
            iv_bt_stop.setVisibility(View.VISIBLE);
        }

    }
    private void gifPlay(boolean isPlay){
        if(isPlay){
            musicPause();
        }else {

            musicPlay();
        }
    }
    public  static  void gifPlayShow(){
        if(isPlay){
            if(gifDrawable!=null) {
//                gifDrawable.start();
                iv_bt_stop.setVisibility(View.GONE);
            }
        }else {
            if(gifDrawable!=null) {
//                gifDrawable.stop();
                iv_bt_stop.setVisibility(View.VISIBLE);
            }
        }
    }
    public  static  Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MUSIC_CHANGE:
//                        getView(R.id.bt_gif).performClick();

                        break;
                    case MUSIC_BLUETOOTH_CLOSE:
                        if(gifDrawable!=null) {
//                            gifDrawable.stop();
                        }
                        if(btservice!=null) {
                            btservice.btAvrPause();
                        }
                        if(iv_bt_stop!=null) {
                            iv_bt_stop.setVisibility(View.VISIBLE);
                        }
                        isPlay=false;
                        break;

                    case MUSIC_BLUETOOTH_OPEN:
                        if(gifDrawable!=null) {
//                            gifDrawable.start();
                        }
                        if(btservice!=null&& isPlay==false){
                        btservice.btAvrPlay();}
                        if(iv_bt_stop!=null) {
                            iv_bt_stop.setVisibility(View.GONE);
                        }
                        isPlay=true;
                        break;
                    case MUSCI_BACK:
                        btservice.btAvrLast();
                        break;
                    case MUSIC_NEXT:
                        btservice.btAvrNext();
                        break;
                    case 11:
                        if(gifDrawable!=null) {
//                            gifDrawable.start();
                        }

                        if(iv_bt_stop!=null) {
                            iv_bt_stop.setVisibility(View.GONE);
                        }
                        isPlay=true;
                        break;
                    case  12:
                        if(gifDrawable!=null) {
//                            gifDrawable.stop();
                        }

                        if(iv_bt_stop!=null) {
                            iv_bt_stop.setVisibility(View.VISIBLE);
                        }
                        isPlay=false;
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
                        App.get().getBtservice().btAvrPause();
                        HomePagerOneFragment.btPaly.setPlay(false);
                        isPlay=false;
                        stopGif();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                case  AudioManager.AUDIOFOCUS_GAIN:
                    Log.d("audioTest", "btmusic gain");
                    try {
//                        if (!audioservice.isDuringNavi()) {
                        if(!isPlay) {
                            App.get().getBtservice().btAvrPlay();
                            HomePagerOneFragment.btPaly.setPlay(true);
                            isPlay=true;
                        }
//                        }
                    }catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case  AudioManager.AUDIOFOCUS_LOSS:
                    try {
                        App.get().getBtservice().btAvrStop();
                        HomePagerOneFragment.btPaly.setPlay(false);
                        isPlay=false;
                        stopGif();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case   AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    try {
                        App.get().getBtservice().btAvrStop();
                        HomePagerOneFragment.btPaly.setPlay(false);
                        isPlay=false;
                        stopGif();
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

//            if (audioManager.requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                return;
//            }

//            setMusicInfo("", "");

//            Log.d("musictest", "btmusic open");
            //}
            setNullViewGone(false);
        } else {
            setNullViewGone(true);
        }
    }
    public static  void setNullViewGone(boolean isShow){
        if(NullView!=null){
        NullView.setVisibility(isShow ? View.VISIBLE : View.GONE);}
        if(isShow){
            stopGif();
        }
    }
}
