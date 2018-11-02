package com.kandi.dell.nscarlauncher.ui.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IKdAudioControlService;
import android.os.IKdBtService;
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
import com.kandi.dell.nscarlauncher.db.dao.MusicCollectionDao;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
import com.kandi.dell.nscarlauncher.ui.music.CursorMusicImage;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.kandi.dell.nscarlauncher.ui.music.adapter.MusicAdapter;
import com.kandi.dell.nscarlauncher.ui.music.adapter.MusicLocalAdapter;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;
import com.kandi.dell.nscarlauncher.ui.music.model.MusicModel;
import com.kandi.dell.nscarlauncher.widget.AddOneEtParamDialog;
import com.kandi.dell.nscarlauncher.widget.CircleImageView;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import static com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty.PAUSE_MSG;
import static com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment.MUSIC_CLOSE;
import static com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment.MUSIC_OPEN;

public class MusicFragment extends BaseFragment {

    private final static int DIRECTION_PREV = 1; // 向前切歌
    private final static int DIRECTION_NEXT = 2; // 向后切歌
    private final static int MUSIC_CHANGE = 3; // 切歌时进行瞬间暂停状态
    private final static int MUSIC_BLUETOOTH_CLOSE = 4; // 蓝牙音乐关闭
    private final static int MUSIC_BLUETOOTH_OPEN = 5; // 蓝牙音乐打开
    private final static int MUSCI_BACK = 6; // 蓝牙音乐上一首
    private final static int MUSIC_NEXT = 7; // 蓝牙音乐下一首
    public final static int  VIEWFRESH =8;//刷新界面
    public static int music_model = 1,dataMode; // 音乐播放循环模式
    static SeekBar music_progress_bar; // 音乐播放进度条
    static int progress = 0; // 记录进度
    static int music_time = 0; // 记录歌曲时间，若无变化，则不更新界面
    static boolean flag_drag = false; // 是否拖动歌曲进度条
    public static boolean flag_first = false;
    static AudioManager audioManager;
    static IKdAudioControlService audioservice ;
    static IKdBtService btservice;
    public static boolean system_flag, am_flag,flag_play,flag_hachage;
    public static CircleImageView circle_image;
    static Context context;
    public static ImageView bt_open, bt_play, bt_back, bt_next, bt_u, bt_music_model, bt_fav;
    public static TextView tv_music_songname, tv_music_singer, music_current_time, music_total_time;
    private  int  currentMode ;
    private   List<Mp3Info> mData = new ArrayList<>();//数据源
    private   List<Mp3Info> mLocalData = new ArrayList<>();//缓存数据源
    private MusicAdapter mAdapter;
    private MusicLocalAdapter musicLocalAdapter;
    public static    DialogLocalMusic dialogLocalMusic;
    public static final String PATH_SDCARDMUSIC = "/sdcard/Music/";
    public int blockCount = 4;
    public List<Mp3Info> favMusic;
    @Override
    public int getContentResId() {
        return R.layout.fragment_music;
    }

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.MUSIC);
    }
    @Override
    public void Resume() {
        super.Resume();
        if(mData!=null&&isSecondResume){
            getMusicData();
        }
    }
    @Override
    public void findView() {
        bt_play=getView(R.id.music_play);
        music_progress_bar=getView(R.id.music_progress_bar);
        music_current_time =getView(R.id.current_time);
        music_total_time=getView(R.id.music_total_time);
        tv_music_songname=getView(R.id.tv_mu_songname);
        tv_music_singer =getView(R.id.tv_mu_singer);
        circle_image =getView(R.id.circle_image);
        bt_music_model=getView(R.id.iv_music_mode);
        bt_next =getView(R.id.music_ringt);
        bt_fav = getView(R.id.music_fav);
        context=  getContext();
    }

    @Override
    public void setListener() {
        setClickListener(R.id.music_left);
        setClickListener(R.id.music_ringt);
        setClickListener(R.id.music_play);
        setClickListener(R.id.iv_music_mode);
        setClickListener(R.id.music_list);
        setClickListener(R.id.music_local_return);
        setClickListener(R.id.music_local_1);
        setClickListener(R.id.music_local_2);
        setClickListener(R.id.music_local_3);
        setClickListener(R.id.music_refresh);
        setClickListener(R.id.music_fav);
    }

    @Override
    public void initView() {

        favMusic = MusicCollectionDao.getAllFav(getContext());
        initSeekBar();
        DialogLocalMusic.setMusicFragment(this);
        getMusicData();
//        dialogLocalMusic.ScanMusic(getContext(),false);
        if(flag_play){
            String albumArt = CursorMusicImage.getImage(getContext(), ((Mp3Info)DialogLocalMusic.data.get(DialogLocalMusic.musicID)).url);
            if (albumArt == null) {
                if(MusicFragment.circle_image!=null) {
                    circle_image.setImageResource(R.mipmap.one);
                }
            } else {
                Bitmap bm = BitmapFactory.decodeFile(albumArt);
                BitmapDrawable bmpDraw = new BitmapDrawable(bm);
                if(MusicFragment.circle_image!=null) {
                    MusicFragment.circle_image.setImageDrawable(bmpDraw);
                }
            }
        }else{
            circle_image.nextRoatate(R.mipmap.one);
        }

        bt_play.setBackgroundResource(flag_play?R.mipmap.ic_play_big:R.mipmap.ic_music_stop);

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
                PreMusic();
                break;
            case R.id.music_ringt:
                NextMusic();
                break;
            case R.id.iv_music_mode:
                setMode();
                break;
            case R.id.music_list:
                currentMode =dataMode;
                setViewVisibilityGone(R.id.music_local,true);
                break;
            case R.id.music_local_return:
                setMusic(dataMode);
                setViewVisibilityGone(R.id.music_local,false);
                break;
            case  R.id.music_local_1:
                dataMode=1;
                getLocalMusicData();
                break;
            case R.id.music_local_2:
                dataMode=2;
                getUsbMusicData();
                break;
            case R.id.music_local_3:
                dataMode=3;
                getMusicColData();
                break;
            case R.id.music_refresh:
//                dialogLocalMusic.ScanMusic(getContext(),false);
//                dialogLocalMusic.ScanVideoMusic(getContext(),1);
                break;
            case R.id.music_fav:
                try {
                    if(DialogLocalMusic.playnow != null && (DialogLocalMusic.playnow.url != null && !"".equals(DialogLocalMusic.playnow.url))){
                        if(MusicCollectionDao.findFavByUrl(getContext(),DialogLocalMusic.playnow.url).size()>0){
                            MusicCollectionDao.deleteFavByUrl(getContext(),DialogLocalMusic.playnow.url);
                            bt_fav.setBackgroundResource(R.mipmap.ic_music_collect);
                        }else{
                            MusicCollectionDao.addFav(getContext(),DialogLocalMusic.playnow);
                            bt_fav.setBackgroundResource(R.mipmap.ic_music_collect_press);
                        }
                        getMusicColData();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

        }
    }
    /*初始化进度条*/
    private void initSeekBar(){
        music_progress_bar.setProgress(0);
        music_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int totaltime = (int) Math.ceil(DialogLocalMusic.playnow.duration);
                int num = (int) Math.ceil(Math.round((float) progress / 100.0 * totaltime));

                if (PlayerService.is_start_speed) {
                    circle_image.roatateStart();
                    bt_play.setBackgroundResource(R.mipmap.ic_play_big);
                    flag_play = true;
                    HomePagerOneFragment.music_name.setText(context.getString(R.string.蓝牙音乐));
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


    /*上一首*/
    public  void PreMusic(){
        if(DialogLocalMusic.data.size()>0) {
            // 非蓝牙音乐播放上一曲
//            if (flag_play) {
//                bt_play.performClick();
//            }
            if (music_model == 2) { // 单曲循环模式不变换音乐图片
                circle_image.resetRoatate();
            } else { // 其他模式
//                 circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_PREV)));
            }
//            circle_image.roatateStart();
            ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));
            MusicModel.getPrevMusic(context, music_model);
        }
    }
    /*下一首*/

    public  void NextMusic(){
        if(DialogLocalMusic.data.size()>0) {
//            if (flag_play) {
//                bt_play.performClick();
//            }
            if (music_model == 2) { // 单曲循环模式不变换音乐图片
                circle_image.resetRoatate();
            } else { // 其他模式
                // circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_NEXT)));
            }
//            circle_image.roatateStart();

            ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_OPEN));
            MusicModel.getNextMusic(context, music_model);
        }
    }
    /*音乐模式*/
    public  static   void setMode(){
        try {
            if (music_model == 1) {
                bt_music_model.setBackgroundResource(R.drawable.music_model_singlecycle);
                music_model = 2;
            } else if (music_model == 2) {
                bt_music_model.setBackgroundResource(R.drawable.music_model_randomcycle);
                music_model = 3;
            } else if (music_model == 3) {
                bt_music_model.setBackgroundResource(R.drawable.music_model_listcycle);
                music_model = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*音乐播放*/
    public   void  play(){
        if (!flag_play) {
            musicPlay(getActivity());
        } else {
            musicPause(getActivity());
        }
    }
    /*播发音乐*/
    public static void musicPlay(Context context){
        if (App.get().getAudioManager().requestAudioFocus(afChangeListener, 12,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                && App.get().getAudioManager().requestAudioFocus(afSystemChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = true;
            am_flag = true;
            if (DialogLocalMusic.data.size() > 0) {
                if(circle_image!=null) {
                    circle_image.roatateStart();
                }
                if(bt_play!=null) {
                    bt_play.setBackgroundResource(R.mipmap.ic_play_big);
                }
                broadcastMusicInfo(context, FlagProperty.PLAY_MSG);
                flag_play = true;
                HomePagerTwoFragment.musicPaly.setPlay(true);
                HomePagerOneFragment.music_name.setText(context.getString(R.string.蓝牙音乐));
                HomePagerTwoFragment.myHandler.sendMessage(HomePagerTwoFragment.myHandler.obtainMessage(MUSIC_OPEN));
            }
        }
    }
    public  static void musicPause(Context context){
        if (App.get().getAudioManager().abandonAudioFocus(afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                && App.get().getAudioManager().abandonAudioFocus(
                afSystemChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = false;
            am_flag = false;

            circle_image.roatatePause();
            bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
            broadcastMusicInfo(context, PAUSE_MSG);
            flag_play = false;
            HomePagerTwoFragment.myHandler.sendMessage(HomePagerTwoFragment.myHandler.obtainMessage(MUSIC_CLOSE));
        }
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
            if("".equals(songname)){
                HomePagerTwoFragment.music_name.setText(context.getString(R.string.本地音乐));
            }else {
                HomePagerTwoFragment.music_name.setText(songname);
            }
            if (!("").equals(singer)) {
                tv_music_singer.setText("- " + singer);
            } else {
                tv_music_singer.setText("");
            }
        }
    }
    // 设置收藏状态信息
    public static void setMusicCol(String url) {
        if(MusicCollectionDao.findFavByUrl(context,url).size()>0){
            bt_fav.setBackgroundResource(R.mipmap.ic_music_collect_press);
        }else{
            bt_fav.setBackgroundResource(R.mipmap.ic_music_collect);
        }
    }
    public static Handler  ViewHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MUSIC_BLUETOOTH_CLOSE:
                    if(bt_play!=null){
                        bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                    }
                    if(circle_image!=null){

                        circle_image.roatatePause();
                    }
                    flag_play=false;
                    break;

                case MUSIC_BLUETOOTH_OPEN:
                    if(bt_play!=null){
                        bt_play.setBackgroundResource(R.mipmap.ic_play_big);
                    }
                    if(circle_image!=null){

                        circle_image.roatateStart();
                    }
                    flag_play=true;
                    HomePagerOneFragment.music_name.setText(context.getString(R.string.蓝牙音乐));
                    break;
            }
        }
    };

    public  Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MUSIC_CHANGE:
                    bt_play.performClick();
                    break;

                case VIEWFRESH:
//                    Log.d("Music ", "getMusiType: " +String.valueOf( HomePagerActivity.mCurFragment.getmType()));
                    if(FragmentType.MUSIC== HomePagerActivity.mCurFragment.getmType()){
                        getMusicData();
                    }
                    break;
                default:
                    break;
            }
        };
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
                        if(circle_image!=null) {
                            circle_image.roatateStart();
                        }
                        if(bt_play!=null) {
                            bt_play.setBackgroundResource(R.mipmap.ic_play_big);
                        }
                        broadcastMusicInfo(context, FlagProperty.PLAY_MSG);
                        flag_play = true;
                        HomePagerOneFragment.music_name.setText(context.getString(R.string.蓝牙音乐));
                        HomePagerTwoFragment.musicPaly.setPlay(true);
                    }
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d("audioTest", "12 loss");
                am_flag = false;
                if(circle_image!=null) {
                    circle_image.roatatePause();
                }
                if(bt_play!=null) {
                    bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                }
                broadcastMusicInfo(context, PAUSE_MSG);
                flag_play = false;
                HomePagerTwoFragment.musicPaly.setPlay(false);
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
                if(circle_image!=null) {
                    circle_image.roatatePause();
                }
                if(bt_play!=null) {
                    bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                }

                broadcastMusicInfo(context, PAUSE_MSG);
                flag_play = false;
                HomePagerTwoFragment.musicPaly.setPlay(false);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d("audioTest", "3 gain");
                system_flag = true;
                if (am_flag) {
                    Log.d("audioTest", "playmusic");
                    if (DialogLocalMusic.data.size() > 0) {
                        if(circle_image!=null) {
                            circle_image.roatateStart();
                        }
                        if(bt_play!=null) {
                            bt_play.setBackgroundResource(R.mipmap.ic_play_big);
                        }
                        broadcastMusicInfo(context, FlagProperty.PLAY_MSG);
                        flag_play = true;
                        HomePagerOneFragment.music_name.setText(context.getString(R.string.蓝牙音乐));
                        HomePagerTwoFragment.musicPaly.setPlay(true);
                    }
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d("audioTest", "3 loss");
                system_flag = false;
                if(circle_image!=null){
                    circle_image.roatatePause();
                }
                if(bt_play!=null) {
                    bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                }
                broadcastMusicInfo(context, PAUSE_MSG);
                flag_play = false;
                HomePagerTwoFragment.musicPaly.setPlay(false);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Log.d("audioTest", "3 loss transient can duck");
                system_flag = false;
                if(circle_image!=null) {
                    circle_image.roatatePause();
                }
                if(bt_play!=null) {
                    bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
                }
                broadcastMusicInfo(context, PAUSE_MSG);
                flag_play = false;
                HomePagerTwoFragment.musicPaly.setPlay(false);
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
        App.get().PauseServiceFMBTMUSic();
        if (audioManager.requestAudioFocus(afChangeListener, 12,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                && audioManager.requestAudioFocus(afSystemChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            system_flag = true;
            am_flag = true;
            circle_image.roatateStart();
            bt_play.setBackgroundResource(R.mipmap.ic_play_big);
            flag_play = true;
            HomePagerOneFragment.music_name.setText(context.getString(R.string.蓝牙音乐));
            HomePagerTwoFragment.myHandler.sendEmptyMessage(1);
        }
    }
    // 设置歌曲进度条信息
    public static void setMusicProgress(int time) {
        time /= 1000; // 得到的是毫秒级别的
        if (music_time != time && !flag_drag) {
            music_time = time;
            int totaltime = (int) Math.ceil(DialogLocalMusic.playnow.duration);
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
        switch (dataMode){
            case 3:
                mData =DialogLocalMusic.ColData;
                DialogLocalMusic.transport(DialogLocalMusic.data, DialogLocalMusic.ColData);
                selectMode(dataMode);

                break;
            case 2:
                mData =DialogLocalMusic.USBData;
                if(mData!=null||mData.size()!=0) {

                    dataMode=2;
                    selectMode(dataMode);
                }
                break;
            default:
                mData =DialogLocalMusic.SDData;
                dataMode = 1;
                selectMode(dataMode);
                if(mData==null||mData.size()==0){
                    mData =DialogLocalMusic.USBData;
                    if(mData!=null||mData.size()!=0) {

                        dataMode=2;
                        selectMode(dataMode);
                    }
                }
                break;
        }


        initRvAdapter(mData);
        initRvLocalAdapter(mData);
//        Log.d("Music ", "getMusicData: " +String.valueOf(mData.size()));
    }

    /*获取本地音乐*/
    private  void getLocalMusicData(){
//      if(flag_play){
////          play();
////      }
//      DialogLocalMusic.musicID=0;
//      Intent intent  =new Intent();
//      intent.setAction("nscar_fresh_sdcard");
//      context.sendBroadcast(intent);

      selectMode(dataMode);
      flag_hachage=true;
      DialogLocalMusic.updateLocalMusic(context);

  }

//

    /*获取Usb音乐*/
    private  void getUsbMusicData(){

        selectMode(dataMode);
        flag_hachage=true;
        initRvLocalAdapter(DialogLocalMusic.USBData);
    }
    /*获取收藏音乐*/
    private  void getMusicColData(){
//      if(flag_play){
////          play();
////      }
//      DialogLocalMusic.musicID=0;
//      Intent intent  =new Intent();
//      intent.setAction("nscar_fresh_sdcard");
//      context.sendBroadcast(intent);
        selectMode(dataMode);
        flag_hachage=true;
        DialogLocalMusic.updateColMusic(context);

    }
    private void setMusic(int dataMode){
        if(dataMode!=currentMode){
            DialogLocalMusic.musicID=0;
        }
        if(dataMode==1) {
            mData = DialogLocalMusic.SDData;
            DialogLocalMusic.transport(DialogLocalMusic.data, DialogLocalMusic.SDData);
        }else if(dataMode==2){
            mData = DialogLocalMusic.USBData;
            DialogLocalMusic.transport(DialogLocalMusic.data, DialogLocalMusic.USBData);
        }else if(dataMode==3){
            mData = DialogLocalMusic.ColData;
            DialogLocalMusic.transport(DialogLocalMusic.data, DialogLocalMusic.ColData);
        }else{
            mData = DialogLocalMusic.USBData;
            DialogLocalMusic.transport(DialogLocalMusic.data, DialogLocalMusic.USBData);
        }
        setTvText(R.id.music_type,dataMode==1?getString(R.string.本地音乐):dataMode==2?getString(R.string.usb):getString(R.string.MusicCol));

        initRvAdapter(mData);
    }
    private void  selectMode(int type){
        setViewSelected(R.id.music_local_1,false);
        setViewSelected(R.id.music_local_2,false);
        setViewSelected(R.id.music_local_3,false);
        switch (type){
            case 1:
                setViewSelected(R.id.music_local_1,true);
                setTvText(R.id.music_type,getString(R.string.本地音乐));

                break;
            case 2 :
                setViewSelected(R.id.music_local_2,true);
                setTvText(R.id.music_type,getString(R.string.usb));
                break;
            case 3:
                setViewSelected(R.id.music_local_3,true);
                setTvText(R.id.music_type,getString(R.string.MusicCol));
                break;
        }
    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<Mp3Info> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView_music);
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
                    PlayerService.isPause = false;
                    Intent i = new Intent(context, PlayerService.class);
                    i.putExtra("MSG", FlagProperty.PLAY_MSG);
                    context.startService(i);
                    listStartPlayMusic();
                    HomePagerTwoFragment.myHandler.sendEmptyMessage(2);
                }


            });

        }else {
            mAdapter.notifyData(data,true);
        }
//        setViewVisibilityGone(R.id.item_music_null,data==null||data.size()==0);
        setViewVisibilityGone(R.id.item_music_null,DialogLocalMusic.SDData.size()==0&&DialogLocalMusic.USBData.size()==0);
        setViewVisibilityGone(R.id.rl_music_nodata,data==null||data.size()==0);
    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvLocalAdapter( List<Mp3Info> data) {
        if (musicLocalAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView_musiclocoal);
            musicLocalAdapter =new MusicLocalAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(musicLocalAdapter);
            }
            musicLocalAdapter.setOnItemClickListener(new MusicLocalAdapter.OnItemClickListener() {

                @Override
                public void onClickMusic(Mp3Info data, int Pos) {
//                    DialogLocalMusic.musicID=Pos;
//                    PlayerService.isPause = false;
//                    Intent i = new Intent(context, PlayerService.class);
//                    i.putExtra("MSG", FlagProperty.PLAY_MSG);
//                    context.startService(i);
//                    listStartPlayMusic();
                }

                @Override
                public void onLongClickMusic(Mp3Info data, int Pos) {
                    if(dataMode==2){
                        ShowDialog(data);
                    }
                }


            });

        }else {
            musicLocalAdapter.notifyData(data,true);
        }
        setViewVisibilityGone(R.id.rl_music_local_nodata,data==null||data.size()==0);
    }


    @Override
    public void onPause() {
        super.onPause();
//        if(flag_play){
//            play();
//        }
    }
    public  static void  reSetMusic(boolean isLater){

        if(context!=null) {
//            broadcastMusicInfo(context,STOP_MSG);
            dialogLocalMusic.ScanMusic(context ,isLater);

        }
    }
    public static  void  stopView(){
        ViewHandler.sendMessage(ViewHandler.obtainMessage(MUSIC_BLUETOOTH_CLOSE));
    }
    //    填写信息dialog
    private  void  ShowDialog( final Mp3Info info){
        AddOneEtParamDialog mAddOneEtParamDialog = AddOneEtParamDialog.getInstance(false,"",2);

        mAddOneEtParamDialog.setOnDialogClickListener(new AddOneEtParamDialog.DefOnDialogClickListener() {
            @Override
            public void onClickCommit(AddOneEtParamDialog dialog, String data) {
                File sourse = new File(info.url);
                long len = sourse.length();
                new CopyFileThread(info.url,PATH_SDCARDMUSIC+info.displayName,0,len).start();
                dialog.dismiss();
                App.get().getCurActivity().initImmersionBar();

            }

            @Override
            public void onClickCancel(AddOneEtParamDialog dialog) {
                App.get().getCurActivity().initImmersionBar();
                dialog.dismiss();
            }
        });

        mAddOneEtParamDialog.show(this.getFragmentManager());
    }

    class CopyFileThread extends Thread{
        private String srcPath;//源文件
        private String destPath;//目标文件地址
        private long start,end;//start起始位置,end结束位置

        public CopyFileThread(String srcPath,String destPath,long start,long end){
            this.srcPath = srcPath;
            this.destPath = destPath;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run(){
            try {
                showLoadingDialog();
                long beginTimes = System.currentTimeMillis();
                Log.i("CopyFileThread","start:"+beginTimes);
                //创建只读的随机访问文件
                RandomAccessFile in = new RandomAccessFile(srcPath,"r");
                //创建可读写的随机访问文件
                RandomAccessFile out = new RandomAccessFile(destPath,"rw");
                //将输入跳转到指定位置
                in.seek(start);
                //从指定位置开始写
                out.seek(start);
                //文件输入通道
                FileChannel inChannel = in.getChannel();
                //文件输出通道
                FileChannel outChannel = out.getChannel();
                //锁住需要操作的区域,false代表锁住
                FileLock lock = outChannel.lock(start,(end-start),false);
                //将字节从此通道的文件传输到给定的可写入字节的输出通道
                inChannel.transferTo(start,(end-start),outChannel);
                lock.release();
                out.close();
                in.close();
                long endTimes = System.currentTimeMillis();
                Log.i("CopyFileThread",""+Thread.currentThread().getName()+"-alltime:"+(endTimes-beginTimes));
                Intent intent  =new Intent();
                intent.setAction("nscar_fresh_sdcard");
                context.sendBroadcast(intent);
                DialogLocalMusic.updateLocalMusic(context);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingDialog();
                    }
                },3500);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
