package com.example.dell.nscarlauncher.ui.home.fragment;


import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;


import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.JumpUtils;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.example.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.example.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.example.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.example.dell.nscarlauncher.ui.music.model.MusicModel;
import com.example.dell.nscarlauncher.widget.PlayControllView;

import static com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty.PAUSE_MSG;
import static com.example.dell.nscarlauncher.ui.music.fragment.MusicFragment.broadcastMusicInfo;
import static com.example.dell.nscarlauncher.ui.music.fragment.MusicFragment.bt_play;
import static com.example.dell.nscarlauncher.ui.music.fragment.MusicFragment.circle_image;

public class HomePagerTwoFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    // 播发控制
    public static PlayControllView musicPaly;

    @Override
    public int getContentResId() {
        return R.layout.fragment_home2;
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public void findView() {
        musicPaly=getView(R.id.music_playcontroll);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    private void setPalyListen(){
        musicPaly.setOnItemClickListener(new PlayControllView.OnItemClickListener() {
            @Override
            public void onClickLeft() {
                App.get().PauseService();
                isMusicFragment();
                if(DialogLocalMusic.data.size()>0) {
                    if ( MusicFragment.flag_play) {

                        musicPaly.center.performClick();
                        MusicPaly(MusicFragment.flag_play);
                    }
                    if ( MusicFragment.music_model == 2) { // 单曲循环模式不变换音乐图片
                        if(circle_image!=null)
                            circle_image.resetRoatate();
                    } else { // 其他模式
                        // circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_NEXT)));
                    }
                    musicPaly.center.performClick();
                    MusicModel.getPrevMusic(getContext(), MusicFragment.music_model);
                }

//                HomePagerActivity.musicFragment.PreMusic();
            }

            @Override
            public void onClickCenter(boolean isPlay) {
                isMusicFragment();
                MusicPaly(isPlay);
            }

            @Override
            public void onClickRight() {
                App.get().PauseService();
                isMusicFragment();
//                HomePagerActivity.musicFragment.NextMusic();
                if(DialogLocalMusic.data.size()>0) {
                    if ( MusicFragment.flag_play) {
                        musicPaly.center.performClick();
                    }
                    if ( MusicFragment.music_model == 2) { // 单曲循环模式不变换音乐图片
                        if(circle_image!=null)
                        circle_image.resetRoatate();
                    } else { // 其他模式
                        // circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_NEXT)));
                    }
                    musicPaly.center.performClick();
                    MusicModel.getNextMusic(getContext(),  MusicFragment.music_model);
                }

            }
        });
    }

    private void  MusicPaly(boolean isPlay){
        if(isPlay){
            App.get().PauseService();

           HomePagerActivity.musicFragment.system_flag = true;
            HomePagerActivity.musicFragment.am_flag = true;
            if (DialogLocalMusic.data.size() > 0) {
                if(circle_image!=null) {
                    circle_image.roatateStart();
                }
                if(bt_play!=null){
                    bt_play.setBackgroundResource(R.mipmap.ic_play_big);
                }

                broadcastMusicInfo(getActivity(), FlagProperty.PLAY_MSG);
                HomePagerActivity.musicFragment.flag_play = true;
            }

        }else {

            HomePagerActivity.musicFragment.system_flag = false;
            HomePagerActivity.musicFragment.am_flag = false;
            if(circle_image!=null) {
                circle_image.roatatePause();
            }
           if(bt_play!=null){
               bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
           }
            broadcastMusicInfo(getActivity(), PAUSE_MSG);
            HomePagerActivity.musicFragment.flag_play = false;
        }
        setPlayControll(isPlay,2);
    }
    public void setPlayControll(boolean isPlay,int mode){
        HomePagerOneFragment.btPaly.setPlay(false);
        HomePagerOneFragment.fmPaly.setPlay(false);
        musicPaly.setPlay(false);

        switch (mode){
            case 1:

                break;
            case 2:
                musicPaly.setPlay(isPlay);
                break;
            default:
        }
    }
    private  void isMusicFragment(){
        if(FragmentType.MUSIC!=homePagerActivity.mCurFragment.getmType()){
            homePagerActivity.switchFragmenthide(HomePagerActivity.musicFragment);
        }
    }
    @Override
    public void setListener() {
        setClickListener(R.id.rl_phone);
        setClickListener(R.id.rl_set);
        setClickListener(R.id.rl_navigation);
        setClickListener(R.id.rl_carcontroll);
        setClickListener(R.id.music);
        setPalyListen();
    }

    @Override
    public void initView() {
        MusicFragment.dialogLocalMusic.ScanMusic(getContext(),false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_phone:
                if(homePagerActivity!=null){
                    homePagerActivity.jumpFragment(FragmentType.PHONE);
                }
                break;
            case R.id.rl_set:
                if(homePagerActivity!=null){
                    homePagerActivity.jumpFragment(FragmentType.SET);
                }
                break;
            case R.id.rl_app:
                if(homePagerActivity!=null){
                    homePagerActivity.jumpFragment(FragmentType.APPLICATION);
                }
                break;
            case R.id.rl_carcontroll:
                if(FlagProperty.BCMStaus==0) {
                    JumpUtils.actAPK(getActivity(), FragmentType.CARCONTROLL);
                }else {
                    Toast.makeText(getActivity(), "BCM未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_navigation:
                if(homePagerActivity!=null){
                    homePagerActivity.openNavi();
                }
                break;
            case R.id.music:
                if(homePagerActivity!=null){
                    homePagerActivity.jumpFragment(FragmentType.MUSIC);
                }
                break;
        }
    }

    public static final  int MUSIC_CLOSE =1;
    private static final  int MUSIC_OPEN =2;
    public static Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MUSIC_CLOSE:
                    musicPaly.setPlay(false);
                    break;

                case MUSIC_OPEN:
                    musicPaly.setPlay(true);
                    break;

                default:
                    break;
            }
        };
    };

}
