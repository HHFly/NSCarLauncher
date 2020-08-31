package com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.btmusic;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.BlueMusicBroadcoast;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;


public class BTMusicFragment  extends BaseFragment {
    private final static int DIRECTION_PREV = 1; // 向前切歌
    private final static int DIRECTION_NEXT = 2; // 向后切歌
    private final static int MUSIC_CHANGE = 3; // 切歌时进行瞬间暂停状态
    public final static int MUSIC_BLUETOOTH_CLOSE = 4; // 蓝牙音乐关闭
    public final static int MUSIC_BLUETOOTH_OPEN = 5; // 蓝牙音乐打开
    private final static int MUSCI_BACK = 6; // 蓝牙音乐上一首
    private final static int MUSIC_NEXT = 7; // 蓝牙音乐下一首
    private final static int MUSIC_SONGNAME = 15;//蓝牙歌曲名

    SeekBar music_progress_bar;
    int progress = 0; // 记录进度
    public  boolean isPlay;
    public ImageView iv_bt_stop;
    public RelativeLayout NullView ;//空界面
    public TextView music_current_time,tv_bt_music_songname,tv_bt_music_singer,music_total_time,bt_blueSet;
    @Override
    public int getContentResId() {
        return R.layout.fragment_bt_music_por;
    }

    @Override
    public void findView() {

        music_progress_bar=getView(R.id.bt_music_progress_bar);
        music_current_time =getView(R.id.bt_music_current_time);
        tv_bt_music_singer=getView(R.id.tv_bt_singer);
        tv_bt_music_songname=getView(R.id.tv_bt_songname);
        music_total_time=getView(R.id.btmusic_total_time);
        bt_blueSet= getView(R.id.bt_blueSet);
        iv_bt_stop=getView(R.id.ctl_iv_center);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        setClickListener(R.id.iv_fm_left);
        setClickListener(R.id.iv_fm_right);
        setClickListener(R.id.ctl_iv_center);
        setClickListener(R.id.bt_blueSet);
    }

    @Override
    public void initView() {
        initSeekBar();
        initName();
        setVisibilityGone(R.id.bt_mic_null,!FlagProperty.flag_bluetooth);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                App.get().getCurActivity().hideFragment();


                break;
            case R.id.ctl_iv_center:
                if (!isPlay) {
                    musicPlay();
                } else {
                    musicPause();
                }


                break;
            case R.id.iv_fm_left:
                musicBack();
                break;
            case R.id.iv_fm_right:
                musicNext();
                break;

            case R.id.bt_blueSet:

                break;
        }
    }
    @Override
    public void Resume() {

        requestAudioFocus();

    }
    /*暂停*/
    public   void musicPause(){
        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
            if (App.get().getAudioManager().abandonAudioFocus(afBTChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                new Thread() {
                    public void run() {
                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
                        isPlay=false;
                    }


                }.start();
            }
        }
    }
    /*播放*/
    public  void musicPlay(){
        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
            int a =App.get().getAudioManager().requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (App.get().getAudioManager().requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                new Thread() {
                    public void run() {


                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));


                    }


                }.start();
            }
        }
    }
    /*上一首*/
    public  void musicBack(){

        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
            if (App.get().getAudioManager().requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                new Thread() {
                    public void run() {

                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));

                        myHandler.sendMessage(myHandler.obtainMessage(MUSCI_BACK));
                    }


                }.start();

            }
        }
    }
    /*下一首*/
    public  void  musicNext(){
        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {
            if (App.get().getAudioManager().requestAudioFocus(afBTChangeListener, 13, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                new Thread() {
                    public void run() {

                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));
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
    public  void setBlueMusicProgress(int time) {
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
    private void initName() {
        if(!("").equals(FlagProperty.SongName)){
            setMusicInfo(FlagProperty.SongName,FlagProperty.SingerName);
        }
    }
    // 设置歌曲信息
    private  void setMusicInfo(String songname, String singer) {
        if (tv_bt_music_songname != null) {
            if(!"null".equals(songname)) {
                tv_bt_music_songname.setText(songname);
            }
            if (("").equals(singer)||"null".equals(singer)) {
                tv_bt_music_singer.setText("");
                tv_bt_music_singer.setVisibility(View.GONE);
            } else {
                tv_bt_music_singer.setText("- " + singer);
                tv_bt_music_singer.setVisibility(View.VISIBLE);
            }
        }
    }
    /**
     * 蓝牙音乐监听器
     */
    public  AudioManager.OnAudioFocusChangeListener afBTChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int i) {
            switch (i){
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.d("audioTest", "btmusic loss transient");

                    try {
                        App.get().getBtservice().btAvrPause();
                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                case  AudioManager.AUDIOFOCUS_GAIN:
                    Log.d("audioTest", "btmusic gain");
                    try {
//                        if (!audioservice.isDuringNavi()) {
                        if(!isPlay) {
                            Log.d("AUDIOFOCUS_GAIN", "run: " +String.valueOf(isPlay));
                            App.get().getBtservice().btAvrPlay();
                            myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));
                        }
//                        }
                    }catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case  AudioManager.AUDIOFOCUS_LOSS:
                    try {
                        App.get().getBtservice().btAvrStop();
                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case   AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    try {
                        App.get().getBtservice().btAvrStop();
                        myHandler.sendMessage(myHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
                        isPlay=false;

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };
    public Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MUSIC_CHANGE:
//                        getView(R.id.bt_gif).performClick();

                        break;
                    case MUSIC_BLUETOOTH_CLOSE:

                        if(App.get().getBtservice()!=null) {
                            App.get().getBtservice().btAvrPause();
                        }
                        if(iv_bt_stop!=null) {
                          iv_bt_stop.setImageResource(R.mipmap.ic_btmusic_default);
                        }
                        isPlay=false;
                        App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.BTMUSICCOLSE);
                        break;

                    case MUSIC_BLUETOOTH_OPEN:

                        if(App.get().getBtservice()!=null&& isPlay==false){
                            App.get().getBtservice().btAvrPlay();
                        }
                        if(iv_bt_stop!=null) {
                           iv_bt_stop.setImageResource(R.mipmap.ic_btmusic_play);
                        }
                        isPlay=true;
                        App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.BTMUSICOPEN);
                        break;
                    case MUSCI_BACK:
                        App.get().getBtservice().btAvrLast();
                        break;
                    case MUSIC_NEXT:
                        App.get().getBtservice().btAvrNext();
                        break;
                    case 11:


                        musicPlay();
                        break;
                    case  12:


                        musicPause();
                        break;
                    case MUSIC_SONGNAME:
                        Bundle bundle =msg.getData();
                        String songname=bundle.getString("songname");
                        String singer =bundle.getString("singer");
                        setMusicInfo(songname,singer);
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
    private void requestAudioFocus() {

        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {

            FlagProperty.flag_bluetooth = true;

            setNullViewGone(false);
        } else {
            setNullViewGone(true);
        }

    }
    public   void setNullViewGone(boolean isShow){
        if(NullView!=null){
            NullView.setVisibility(isShow ? View.VISIBLE : View.GONE);}

    }
}
