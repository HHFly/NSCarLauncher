package com.example.dell.nscarlauncher.ui.home.fragment;


import android.os.RemoteException;
import android.view.View;


import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.JumpUtils;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.example.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.example.dell.nscarlauncher.ui.music.DialogLocalMusic;
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
                HomePagerActivity.musicFragment.PreMusic();
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
                HomePagerActivity.musicFragment.NextMusic();
            }
        });
    }

    private void  MusicPaly(boolean isPlay){
        if(isPlay){
            App.get().PauseService();

           HomePagerActivity.musicFragment.system_flag = true;
            HomePagerActivity.musicFragment.am_flag = true;
            if (DialogLocalMusic.data.size() > 0) {
                circle_image.roatateStart();
                bt_play.setBackgroundResource(R.mipmap.ic_play_big);
                broadcastMusicInfo(getActivity(), FlagProperty.PLAY_MSG);
                HomePagerActivity.musicFragment.flag_play = true;
            }

        }else {

            HomePagerActivity.musicFragment.system_flag = false;
            HomePagerActivity.musicFragment.am_flag = false;

            circle_image.roatatePause();
            bt_play.setBackgroundResource(R.mipmap.ic_music_stop);
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
                JumpUtils.actAPK(getActivity(),FragmentType.CARCONTROLL);
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


}
