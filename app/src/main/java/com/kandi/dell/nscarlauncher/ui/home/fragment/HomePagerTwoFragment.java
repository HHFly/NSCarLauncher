package com.kandi.dell.nscarlauncher.ui.home.fragment;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.JumpUtils;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.music.model.MusicModel;
import com.kandi.dell.nscarlauncher.widget.PlayControllView;

import static com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment.circle_image;

public class HomePagerTwoFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    // 播发控制
    public static PlayControllView musicPaly;
    public static  int backbox =0x02;//0x01为开启状态，0x02为关闭状态。
    public static int  centerlock =0x02;//0x01为开启状态，0x02为关闭状态。
    private static ImageView iv_backbox,iv_cenlock;
    public static  boolean isBackboxOpen,isCenterlockOpen;
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
        iv_backbox=getView(R.id.iv_backbox);
        iv_cenlock =getView(R.id.iv_cenlock);
    }
    @Override
    public void setListener() {
        setClickListener(R.id.rl_phone);
        setClickListener(R.id.rl_set);
        setClickListener(R.id.rl_navigation);
        setClickListener(R.id.rl_carcontroll);
        setClickListener(R.id.music);
        setClickListener(R.id.iv_backbox);
        setClickListener(R.id.iv_cenlock);
        setClickListener(R.id.iv_window);
        setClickListener(R.id.nav_home);
        setClickListener(R.id.nav_company);
        setClickListener(R.id.iv_phone_logo);
        setPalyListen();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.iv_backbox:
//                后备箱要注意只控制开锁，关锁不需要控制。
                if(FlagProperty.BCMStaus==0) {
                    HomePagerActivity.setBackBox(true);
                }else {
                    Toast.makeText(getActivity(), R.string.BCM未连接, Toast.LENGTH_SHORT).show();
                }

                    break;
            case R.id.iv_cenlock:
//
                if(FlagProperty.BCMStaus==0) {
                    isCenterlockOpen=!isCenterlockOpen;
                    HomePagerActivity.setDoorLock(isCenterlockOpen);
                }else {
                    Toast.makeText(getActivity(), R.string.BCM未连接, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.iv_window:
                HomePagerActivity.OneKeyWindowOpen();
                break;
            case R.id.rl_phone:
                HomePagerActivity.jumpFragment(FragmentType.PHONE);
//                if(homePagerActivity!=null){
//                    homePagerActivity.jumpFragment(FragmentType.PHONE);
//                }
                break;
            case  R.id.iv_phone_logo:
                HomePagerActivity.jumpFragment(FragmentType.PHONE);
                break;
            case R.id.rl_set:
                HomePagerActivity.jumpFragment(FragmentType.SET);
                break;
            case R.id.rl_app:
                HomePagerActivity.jumpFragment(FragmentType.APPLICATION);
                break;
            case R.id.rl_carcontroll:
                if(FlagProperty.BCMStaus==0) {
                    JumpUtils.actAPK(getActivity(), FragmentType.CARCONTROLL);
                }else {
                    Toast.makeText(getActivity(), R.string.BCM未连接, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_navigation:
                if(homePagerActivity!=null){
                    homePagerActivity.openNavi();
                }
                break;
            case R.id.music:
                HomePagerActivity.jumpFragment(FragmentType.MUSIC);
                break;
            case R.id.nav_home:
                NavBroasd(0);
                break;
            case R.id.nav_company:
                NavBroasd(1);
                break;
        }
    }
    private void setPalyListen(){
        musicPaly.setOnItemClickListener(new PlayControllView.OnItemClickListener() {
            @Override
            public void onClickLeft() {
                App.get().PauseServiceFMBTMUSic();
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
                App.get().PauseServiceFMBTMUSic();
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
            App.get().PauseServiceFMBTMUSic();

            MusicFragment.musicPlay(getActivity());

        }else {

          MusicFragment.musicPause(getActivity());
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
    public void initView() {
//        MusicFragment.dialogLocalMusic.ScanMusic(getContext(),false);
//        MusicFragment.dialogLocalMusic.ScanVideoMusic(getContext(),false,1);
    }


    private static  void setBackbox(){
        switch (backbox){
            case 0x01:
                iv_backbox.setImageResource(R.mipmap.ic_car_1);
                break;
            case 0x02:
                iv_backbox.setImageResource(R.mipmap.ic_car_1_close);
                break;

        }
    }
    private static  void  setIv_cenlock(){
        switch (centerlock){
            case 0x01:
                iv_cenlock.setImageResource(R.mipmap.ic_car_2);
                break;
            case 0x02:
                iv_cenlock.setImageResource(R.mipmap.ic_car_1_white);
                break;

        }
    }
    public static final  int MUSIC_CLOSE =1;
    public static final  int MUSIC_OPEN =2;
    public static final  int BACKBOX =3;
    public static final  int CENTERLOCK=4;
    public static Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MUSIC_CLOSE:
                    musicPaly.setPlay(false);
                    break;

                case MUSIC_OPEN:
                    musicPaly.setPlay(true);
                    break;
                case  BACKBOX:
                    setBackbox();
                    break;
                case  CENTERLOCK:
                    setIv_cenlock();
                    break;
                default:
                    break;
            }
        };
    };

    /*高德地图导航 0：回家
1：回公司*/
    private void NavBroasd(int type){
        Intent intent = new Intent();
        intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV");
        intent.putExtra("KEY_TYPE", 10070);
        intent.putExtra("EXTRA_TYPE", type);
        getActivity().sendBroadcast(intent);
    }
}
