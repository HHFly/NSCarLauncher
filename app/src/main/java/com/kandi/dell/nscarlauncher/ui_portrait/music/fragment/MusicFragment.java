package com.kandi.dell.nscarlauncher.ui_portrait.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.kandi.dell.nscarlauncher.ui.music.CursorMusicImage;
import com.kandi.dell.nscarlauncher.ui_portrait.music.adapter.MusicMainAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.music.dialog.DialogLrc;
import com.kandi.dell.nscarlauncher.ui_portrait.music.model.Mp3Info;
import com.kandi.dell.nscarlauncher.ui_portrait.music.model.MusicModel;
import com.kandi.dell.nscarlauncher.ui_portrait.music.service.PlayerService;

import java.io.File;
import java.util.List;


public class MusicFragment extends BaseFragment {
    private final static int MUSIC_CHANGE = 3; // 切歌时进行瞬间暂停状态
    public final static int MUSIC_BLUETOOTH_CLOSE = 4; // 音乐关闭
    public final static int MUSIC_BLUETOOTH_OPEN = 5; // 音乐打开
    public final static int  STOP =8;//
    public final static String MUSICMODEL = "music_model";//音乐播放模式
    public final static String MUSICPATH = "music_path";//音乐播放路径
    public final static String MUSICDATAMODE = "music_dataMode";//音乐播放来源
    public final static String MUSICID = "music_id";//音乐播放位置
    public final static String MUSICPROGRESS = "musicprogress";//音乐播放进度
    public BaseFragment mCurFragment;//当前页
    public SeekBar music_progress_bar;
    public ImageView bt_play,bt_music_model,gramophoneView;
    public TextView tv_music_songname, tv_music_singer, music_current_time, music_total_time;
    public  boolean flag_play,flag_drag,flag_first,system_flag, am_flag;
   public int music_model = 1,dataMode; // 音乐播放循环模式
    public   int Progress = 0; // 记录进度
    int music_time = 0; // 记录歌曲时间，若无变化，则不更新界面
    public  boolean recoveryLast = false;//记忆上次播放标志位
    public MusicMainAdapter mAdapter;
//    private DialogLrc dialogLrc;
    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.MUSIC);
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_music_por;
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        setClickListener(R.id.music_left);
        setClickListener(R.id.music_ringt);
        setClickListener(R.id.music_play);
        setClickListener(R.id.iv_music_mode);
        setClickListener(R.id.music_list);
        setClickListener(R.id.music_lrc);
        setClickListener(R.id.music_fav);
    }

    @Override
    public void initView() {
        setmType(FragmentType.MUSIC);
        initSeekBar();
        //回复上次播放
        recoveryLast();
        //初始化view播放状态
        initPlayView();
        //初始化歌词弹框
//        getDialogLrc();
    }

    @Override
    public void findView() {
        music_progress_bar=getView(R.id.music_progress_bar);
        bt_play=getView(R.id.music_play);
        gramophoneView=getView(R.id.iv_music_thunb);
        music_current_time =getView(R.id.current_time);
        music_total_time=getView(R.id.music_total_time);
        tv_music_songname=getView(R.id.tv_mu_songname);
        tv_music_singer =getView(R.id.tv_mu_singer);
        bt_music_model=getView(R.id.iv_music_mode);
    }

    @Override
    public void Resume() {
        super.Resume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
              App.get().getCurActivity().hideFragment();
                break;
            case R.id.music_play:
                play();
                break;
            case R.id.music_left:
                PreMusic();
                break;
            case R.id.music_ringt:
                NextMusic();
                break;
            case R.id.iv_music_mode:
                setMode();
                break;
            case R.id.music_list:
                App.get().getCurActivity().getDialogLocalMusicD().show();
                break;
            case R.id.music_lrc:
                App.get().getCurActivity().getDialogLrc().show();
                break;
            case R.id.music_fav:
                if (App.get().getCurActivity().getDialogLocalMusicD().Playnow==null){
               return;
               }
                App.get().getCurActivity().getDialogLocalMusicD().Playnow.changeFav();
                setViewSelected(R.id.music_fav,App.get().getCurActivity().getDialogLocalMusicD().Playnow.isFav());
                break;

        }
    }

    /*初始化进度条*/
    private void initSeekBar(){
        music_progress_bar.setProgress(0);
        music_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if( App.get().getCurActivity().getDialogLocalMusicD().data.size()>0&& App.get().getCurActivity().getDialogLocalMusicD().Playnow!=null) {
                    int totaltime = (int) Math.ceil( App.get().getCurActivity().getDialogLocalMusicD().Playnow.duration);
                    int num = (int) Math.ceil(Math.round((float) Progress / 100.0 * totaltime));

                    if (PlayerService.is_start_speed) {

                        bt_play.setImageResource(R.mipmap.ic_music_play);
                        flag_play = true;

                    }

                    Intent i = new Intent(getActivity(), PlayerService.class);
                    i.putExtra("progress", num);
                    i.putExtra("MSG", FlagProperty.PROGRESS_CHANGE);
                    getActivity().startService(i);
                    flag_drag = false;
                    flag_first = true;
                }else {
                    music_progress_bar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                flag_drag = true;

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Progress = progress;

            }
        });

    }
    /*记忆上次音乐播放状态*/
    public void recoveryLast(){
        String musicpath = SPUtil.getInstance(getContext()).getString(MUSICPATH);

        music_model = SPUtil.getInstance(getContext()).getInt(MUSICMODEL,music_model);
        dataMode = SPUtil.getInstance(getContext()).getInt(MUSICDATAMODE,dataMode);
        if (music_model == 1) {
            bt_music_model.setBackgroundResource(R.drawable.selector_musicmode_circle);
        } else if (music_model == 2) {
            bt_music_model.setBackgroundResource(R.drawable.selector_musicmode_single);
        } else if (music_model == 3) {
            bt_music_model.setBackgroundResource(R.drawable.selector_musicmode_random);
        }

        if(musicpath !=null && !musicpath.equals("")){
            if(new File(musicpath).exists()){
                int music_id = SPUtil.getInstance(getContext()).getInt(MUSICID,App.get().getCurActivity().getDialogLocalMusicD().musicDiverID);
                try{
                    List<Mp3Info> data =App.get().getCurActivity().getScanService().getDatabyMode(dataMode);
                    if( data ==null||data.size()==0){
                        return;
                    }

                    if((data.get(music_id).url).equals(musicpath)){
                        recoveryLast = true;
                        App.get().getCurActivity().getDialogLocalMusicD().musicDiverID = music_id;

                    }
                }catch (IndexOutOfBoundsException e){
                    App.get().getCurActivity().getDialogLocalMusicD().musicDiverID = 0;
                }
            }
        }
    }
    private void initPlayView(){
        if(flag_play){
            String albumArt = CursorMusicImage.getImage(getContext(), ((Mp3Info)App.get().getCurActivity().getDialogLocalMusicD().data.get(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID)).url);
            if (albumArt == null) {
                if(gramophoneView!=null) {
                    gramophoneView.setImageResource(R.mipmap.ic_music_default);
                }
            } else {
                Bitmap bm = BitmapFactory.decodeFile(albumArt);
                if(gramophoneView!=null) {
                    gramophoneView.setImageBitmap(bm);

                }
            }
            String musicName = ((Mp3Info)App.get().getCurActivity().getDialogLocalMusicD().data.get(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID)).title;
            String artist = ((Mp3Info)App.get().getCurActivity().getDialogLocalMusicD().data.get(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID)).artist;
            App.get().getCurActivity().getMusicFragment().setMusicInfo(musicName, artist);
        }else{
            gramophoneView.setImageResource(R.mipmap.ic_music_default);
        }
        bt_play.setBackgroundResource(flag_play?R.mipmap.ic_music_play:R.mipmap.ic_music_stop);
        initRvAdapter(App.get().getCurActivity().getDialogLocalMusicD().data);
    }
    /*音乐播放*/
    public   void  play(){
        if (!flag_play) {
            musicPlay(App.get().getCurrentActivity());
        } else {
            musicPause(App.get().getCurrentActivity());
        }
    }
    /*播发音乐*/
    public  void musicPlay(Context context){
        if(App.get().getCurActivity().getDialogLocalMusicD().data.size()==0) {
            return;
        }
        if ( App.get().getAudioManager().requestAudioFocus(afSystemChangeListener, 12, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = true;
            am_flag = true;
//            if (homePagerActivity.getDialogLocalMusicD().data.size() > 0) {
            ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));
            if(bt_play!=null) {
                bt_play.setBackgroundResource(R.mipmap.ic_music_play);
            }
            if(recoveryLast){
                broadcastMusicInfoChange(context, FlagProperty.PROGRESS_CHANGE,SPUtil.getInstance(context).getInt(MUSICPROGRESS,0));
                recoveryLast = false;
            }else{
                broadcastMusicInfo(context, FlagProperty.PLAY_MSG);
            }

            flag_play = true;

        }
//        }
    }
    public   void musicPause(Context context){
        if(App.get().getCurActivity().getDialogLocalMusicD().Playnow==null) {
            return;
        }
        if (App.get().getAudioManager().abandonAudioFocus(afSystemChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = false;
            am_flag = false;

            ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
            if(bt_play!=null) {
                bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
            }
            broadcastMusicInfo(context, FlagProperty.PAUSE_MSG);
            flag_play = false;

        }
    }
    /**
     * 本地系统音乐监听器
     */
    public  AudioManager.OnAudioFocusChangeListener afSystemChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Log.i("testtest","=====musicPlay===requestAudioFocus========AUDIOFOCUS_LOSS_TRANSIENT");
                system_flag = false;

                if(bt_play!=null) {
                    bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                }

                broadcastMusicInfo(App.get().getCurrentActivity(), FlagProperty.PAUSE_MSG);
                flag_play = false;
                ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.i("testtest","=====musicPlay===requestAudioFocus========AUDIOFOCUS_GAIN");
                Log.d("audioTest", "3 gain");
                system_flag = true;
                if (am_flag) {
                    Log.d("audioTest", "playmusic");
                    if (App.get().getCurActivity().getDialogLocalMusicD().data.size() > 0) {

                        if(bt_play!=null) {
                            bt_play.setBackgroundResource(R.mipmap.ic_music_play);
                        }
                        broadcastMusicInfo(App.get().getCurrentActivity(), FlagProperty.PLAY_MSG);
                        flag_play = true;

                    }
                    ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.i("testtest","=====musicPlay===requestAudioFocus========AUDIOFOCUS_LOSS");
                system_flag = false;

                if(bt_play!=null) {
                    bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                }
                broadcastMusicInfo(App.get().getCurrentActivity(), FlagProperty.PAUSE_MSG);
                flag_play = false;
                ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Log.i("testtest","=====musicPlay===requestAudioFocus========AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                Log.d("audioTest", "3 loss transient can duck");
                system_flag = false;

                if (bt_play != null) {
                    bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                }
                broadcastMusicInfo(App.get().getCurrentActivity(), FlagProperty.PAUSE_MSG);
                flag_play = false;
                ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
            }
        }
    };
    /*上一首*/
    public  void PreMusic(){
        if(App.get().getCurActivity().getDialogLocalMusicD().data.size()>0) {
            // 非蓝牙音乐播放上一曲
//            if (flag_play) {
//                bt_play.performClick();
//            }
            if (music_model == 2) { // 单曲循环模式不变换音乐图片
//                gramophoneView.resetRoatate();
            } else { // 其他模式
//                 circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_PREV)));
            }
//            circle_image.roatateStart();
            ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));
            MusicModel.getPrevMusic(App.get().getCurrentActivity(), music_model);
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    /*下一首*/

    public  void NextMusic(){

        if(App.get().getCurActivity().getDialogLocalMusicD().data.size()>0) {
//            if (flag_play) {
//                bt_play.performClick();
//            }
            if (music_model == 2) { // 单曲循环模式不变换音乐图片
//                circle_image.resetRoatate();
            } else { // 其他模式
                // circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_NEXT)));
            }
//            circle_image.roatateStart();

            ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));
            MusicModel.getNextMusic(App.get().getCurrentActivity(), music_model);
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    /*音乐模式*/
    public     void setMode(){
        try {
            if (music_model == 1) {
                bt_music_model.setBackgroundResource(R.drawable.selector_musicmode_single);
                music_model = 2;
            } else if (music_model == 2) {
                bt_music_model.setBackgroundResource(R.drawable.selector_musicmode_random);
                music_model = 3;
            } else if (music_model == 3) {
                bt_music_model.setBackgroundResource(R.drawable.selector_musicmode_circle);
                music_model = 1;
            }
            SPUtil.getInstance(getContext()).putInt(MUSICMODEL,music_model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Handler ViewHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MUSIC_BLUETOOTH_CLOSE:
                    App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.MUSICCOLSE);
                    if(bt_play!=null){
                        bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                    }

                    flag_play=false;

                    break;

                case MUSIC_BLUETOOTH_OPEN:
                    App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.MUSICOPEN);
                    if(bt_play!=null){
                        bt_play.setBackgroundResource(R.mipmap.ic_music_play);
                    }
                    if(App.get().getCurActivity().getDialogLocalMusicD().Playnow != null){
                        setViewSelected(R.id.music_fav,App.get().getCurActivity().getDialogLocalMusicD().Playnow.isFav());
                    }
                    flag_play=true;

                    break;
                case MUSIC_CHANGE:
                    bt_play.performClick();
                    break;

                case STOP:
                    if(App.get().getCurActivity().getDialogLocalMusicD().Playnow==null) {
                        return;
                    }

                    system_flag = false;
                    am_flag = false;

                    App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.MUSICCOLSE);
                    if(bt_play!=null) {
                        bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                    }
                    broadcastMusicInfo(getContext(), FlagProperty.PAUSE_MSG);
                    flag_play = false;


                    break;
            }
        }
    };
    // 发送音乐变更信息
    public  void broadcastMusicInfo(Context context, int msg) {
        Intent i = new Intent(context, PlayerService.class);
        i.putExtra("MSG", msg);
        context.startService(i);
    }
    // 发送音乐变更信息
    public  void broadcastMusicInfoChange(Context context, int msg, int currentTime) {
        Intent i = new Intent(context, PlayerService.class);
        i.putExtra("MSG", msg);
        i.putExtra("progress",currentTime);
        context.startService(i);
    }
    // 设置歌曲信息
    public  void setMusicInfo(String songname, String singer) {
        if (tv_music_songname != null) {
            tv_music_songname.setText(songname);

            if (!("").equals(singer)) {
                tv_music_singer.setText( singer);
            } else {
                tv_music_singer.setText("");
            }
        }
    }
    // 设置歌曲进度条信息
    public  void setMusicProgress(int time) {
        time /= 1000; // 得到的是毫秒级别的
        if (music_time != time && !flag_drag) {
            music_time = time;
            int totaltime = (int) Math.ceil(App.get().getCurActivity().getDialogLocalMusicD().Playnow.duration);
            totaltime /= 1000;
            if (time > totaltime) {
                time = totaltime; // 避免毫秒级时间产生时间误差
            }
            if (totaltime > 0) {
                int progress = (int) Math.ceil(time * 100 / totaltime);
                if (flag_first) {
                    if (progress > Progress) {
                        if(music_progress_bar != null){
                            music_progress_bar.setProgress(progress);
                        }
                        App.get().getCurActivity().music_progress_bar.setProgress(progress);
                        flag_first = false;
                    }
                } else {
                    if(music_progress_bar != null){
                        music_progress_bar.setProgress(progress);
                    }
                    App.get().getCurActivity().music_progress_bar.setProgress(progress);
                }
                if(music_current_time != null){
                    music_current_time.setText("" + getTime(time / 60) + ":" + getTime(time % 60));
                }
                App.get().getCurActivity().setTvText(R.id.music_current_time,getTime(time / 60) + ":" + getTime(time % 60));
                if(music_total_time != null){
                    music_total_time.setText("" + getTime(totaltime / 60) + ":" + getTime(totaltime % 60));
                }
                App.get().getCurActivity().setTvText(R.id.music_total_time,getTime(totaltime / 60) + ":" + getTime(totaltime % 60));
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
    public   void  stopView(){
        ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
    }
   /**
     * 选择Fragment
     *
     * @param fragment
     */

    /**
     * 初始化adapter
     *
     *
     */
    public void initRvAdapter( List<Mp3Info> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView_music);
            mAdapter =new MusicMainAdapter(data);
            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new MusicMainAdapter.OnItemClickListener() {


                @Override
                public void onClickMusic(Mp3Info data, int Pos) {
                    recoveryLast = false;
                    App.get().getCurActivity().getDialogLocalMusicD().musicDiverID=Pos;
                    PlayerService.isPause = false;
                    Intent i = new Intent(getContext(), PlayerService.class);
                    i.putExtra("MSG", FlagProperty.PLAY_MSG);
                    getContext().startService(i);
                    listStartPlayMusic();
                    mAdapter.notifyDataSetChanged();

                }
            });

        }else {
            mAdapter.notifyData(data,true);
        }


        setViewVisibilityGone(R.id.rl_music_nodata,data==null||data.size()==0);
    }
    // 开始播放音乐
    public  void listStartPlayMusic() {
        if ( App.get().getAudioManager().requestAudioFocus(afSystemChangeListener, 12,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = true;
            am_flag = true;

            bt_play.setBackgroundResource(R.mipmap.ic_music_play);
            flag_play = true;

        }
    }
}
